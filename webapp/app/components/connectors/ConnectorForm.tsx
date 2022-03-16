import { FormFieldsDto } from "@/app/common/dtos/FormFieldsDto";
import { Form, Formik, FormikValues, yupToFormErrors } from "formik";
import InputField from "@/app/components/forminputs/InputField";
import DynamicFormFields from "@/app/components/connectors/DynamicFormFields";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import React, { useEffect, useState } from "react";
import formHandler from "@/app/common/utils/formHandler";
import appsService from "@/app/services/appsService";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { AccessType } from "@/app/common/enums/AccessType";
import { ConnectorCategory } from "@/app/common/utils/types";
import warehouseService from "@/app/services/warehouseService";
import { useRouter } from "next/router";
import routerUtils from "@/app/common/utils/routerUtils";
import { Card } from "react-bootstrap";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { ConnectorDto } from "@/app/common/dtos/ConnectorDto";
import stringUtils from "@/app/common/utils/stringUtils";
import { useSession } from "@/app/common/context/sessionContext";
import ReactMarkdown from "react-markdown";
import * as yup from "yup";
import dynamicFormUtils from "@/app/common/utils/dynamicFormUtils";
import { valueEventAriaMessage } from "react-select/src/accessibility";
import { ConnectorCategoryLabel } from "@/app/common/enums/ConnectorCategory";

const API_BASE = process.env.API_BASE || "";

export interface ConnectorFormProps {
  appBaseUrl: string;
  editConnector?: ConnectorDto | null;
  category: ConnectorCategory;
  connectorType: string;
  oauthCallback?: string;
  accessType: AccessType;
  onFinish: (id: number) => void;
}

const ConnectorForm = ({
  appBaseUrl,
  editConnector,
  category,
  connectorType,
  accessType,
  oauthCallback,
  onFinish,
}: ConnectorFormProps) => {
  const router = useRouter();
  const { pipelineWizContext } = usePipelineWizContext();
  const wizardStep = routerUtils.getString(router.query.wizardStep);
  const id = routerUtils.getInt(router.query.id);
  const type = connectorType || routerUtils.getInt(router.query.type);
  const [formFields, setFormFields] = useState<FormFieldsDto | undefined>();
  const { isOss } = useSession();
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    if (!pipelineWizContext) return;
    if (id) {
      onFinish(id);
      return;
    }
    if (type) {
      const fetcher =
        category === "App"
          ? appsService.formFields
          : warehouseService.formFields;
      fetcher(type).then(({ data }) => {
        setLoading(false);
        setFormFields(data);
      });
    }
  }, [!!pipelineWizContext, type, id]);

  const isOauth = accessType === AccessType.OAUTH;
  const createConnector: any =
    category === "Warehouse"
      ? warehouseService.create
      : isOauth
      ? appsService.createOauth
      : appsService.create;
  const updateConnector: any =
    category === "Warehouse"
      ? warehouseService.update
      : isOauth
      ? appsService.updateOauth
      : appsService.update;
  const fetcher = editConnector
    ? (data: any) => updateConnector(editConnector.id, data)
    : createConnector;
  const onSubmit = formHandler(
    isOss,
    {
      id: "connector_form",
      pickFieldsForEvent: ["name", "config.type"],
      dataLayer: { connectorCategory: category },
    },
    fetcher,
    (res: any) => {
      if (res?.redirectUrl) {
        window.location = res.redirectUrl;
      } else {
        bannerNotificationService.success("Success");
        onFinish(res.id);
      }
    },
    undefined,
    (values: any): any => {
      if (isOauth) {
        const defaultOauthUrl = `${appBaseUrl}${
          (location?.pathname || router.pathname) +
          "?wizardStep=" +
          wizardStep +
          "&type=" +
          values.config.type +
          "&success=1"
        }`;

        return {
          ...values,
          successUrl: oauthCallback
            ? `${appBaseUrl}${oauthCallback}`
            : defaultOauthUrl,
          failureUrl: `${appBaseUrl}${
            oauthCallback +
            "?wizardStep=" +
            wizardStep +
            "&type=" +
            values.config.type +
            "&failed=1"
          }`,
          serverUrl: `${appBaseUrl}${API_BASE}`,
        };
      }
      return values;
    }
  );

  const submitLabel = !editConnector
    ? `Save & Continue`
    : isOauth
    ? "Reauthorize"
    : "Save";

  const getCodeBlock = (formFields: FormFieldsDto, values: FormikValues) => {
    if (
      formFields.codeBlock?.dependencies.filter(
        (dependencyRef) => values.config[dependencyRef]
      ).length !== formFields.codeBlock?.dependencies.length
    ) {
      return null;
    }
    return (
      <div className="mt-3">
        <label> {formFields?.codeBlock?.title}</label>
        {formFields.codeBlock?.snippets.map((codeSnippet) => (
          <Card.Body>
            <Card.Header>
              <strong>{codeSnippet.title}</strong>
            </Card.Header>
            <Card.Footer>
              {stringUtils.replaceTemplate(codeSnippet.code, values.config)}
            </Card.Footer>
          </Card.Body>
        ))}
      </div>
    );
  };

  const getHelpText = (formFields: FormFieldsDto, values: FormikValues) => {
    if (
      formFields.helpText?.dependencies.filter(
        (dependencyRef) => values.config[dependencyRef]
      ).length !== formFields.helpText?.dependencies.length
    ) {
      return null;
    }
    return (
      <div className="mb-1">
        <ReactMarkdown>
          {stringUtils.replaceTemplate(
            formFields.helpText!.value,
            values.config
          )}
        </ReactMarkdown>
      </div>
    );
  };
  return (
    <Formik
      key={`fields-${!!formFields}`}
      initialValues={
        editConnector
          ? dynamicFormUtils.getInitialValues(
              formFields,
              "config",
              editConnector
            )
          : dynamicFormUtils.getInitialValues(formFields, "config", {
              name: "",
              config: {
                type: connectorType,
              },
            })
      }
      validate={(values: any) =>
        dynamicFormUtils.getValidationErrors(formFields, "config", values, {
          name: yup.string().required("Required"),
        })
      }
      onSubmit={onSubmit}
      className="animate"
    >
      {({ values, isSubmitting, setFieldValue }) => (
        <Form>
          <InputField
            type="text"
            name="name"
            title={`${ConnectorCategoryLabel[category]} ${category} Name`}
            placeholder={`Enter a name`}
            required
          />
          <InputField type="hidden" name="config.type" title="Type" />
          {loading && <div className="w-50 linear-background"></div>}
          {connectorType && (
            <DynamicFormFields
              namePrefix="config"
              skipNames={["name"]}
              formFields={formFields}
              setFieldValue={setFieldValue}
              values={values}
              dataFetcher={(optionsRef) =>
                category === "Warehouse"
                  ? warehouseService.configOptions(optionsRef, values)
                  : appsService.configOptions(optionsRef, values)
              }
            />
          )}
          {formFields?.codeBlock && getCodeBlock(formFields, values)}
          {formFields?.helpText && getHelpText(formFields, values)}
          <ButtonSubmit
            submitting={isSubmitting}
            className="mb-3"
            connectorType={connectorType}
          >
            {submitLabel}
          </ButtonSubmit>
        </Form>
      )}
    </Formik>
  );
};

export default ConnectorForm;
