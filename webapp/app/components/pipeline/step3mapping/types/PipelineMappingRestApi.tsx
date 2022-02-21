import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React, { useEffect, useRef, useState } from "react";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { Button, Col, ListGroup, Row, Table } from "react-bootstrap";
import InputSelect from "@/app/components/forminputs/InputSelect";
import InputField from "@/app/components/forminputs/InputField";
import _ from "lodash";
import { Formik, Form } from "formik";
import Loading from "@/app/components/common/Loading";
import { PipelineMappingDto } from "@/app/common/dtos/PipelineCreateRequestDto";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import { useSession } from "@/app/common/context/sessionContext";
import pipelineMappingUtils from "@/app/common/utils/pipelineMappingUtils";
import formInputUtils from "@/app/common/utils/formInputUtils";
import { HttpMethod, RestApiMethodLabel } from "@/app/common/enums/HttpMethod";
import { PipelineMappingType } from "@/app/common/enums/PipelineMappingType";
import * as yup from "yup";
import TextareaAutosize from "react-textarea-autosize";
import Select from "react-select";
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import pipelineService from "@/app/services/pipelineService";

interface PipelineMappingRestApiProps extends PipelineWizardStepProps {
  pipelineSchema: PipelineSchemaResponseDto | undefined;
  isLoading: boolean;
}

export interface ReactSelectOption {
  value: any;
  label: string;
}

const PipelineMappingRestApi = ({
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
  pipelineSchema,
  isLoading,
}: PipelineMappingRestApiProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  let [headerKeys, setHeaderKeys] = useState<string[]>([""]);
  const { isOss } = useSession();

  let templateareaRef = useRef<HTMLTextAreaElement>(null);
  const [templateValue, setTemplateValue] = useState("");

  if (!pipelineWizContext) {
    return <Loading />;
  }
  let warehouseSchemaFields = pipelineMappingUtils.getSchemaFieldsAsOptions(
    pipelineSchema?.warehouseSchema
  );

  const toReactSelectOption = (o: SelectOptionDto): ReactSelectOption => ({
    value: o.value,
    label: o.title,
  });

  const [testResults, setTestResults] = useState(false);

  const nextStep = (): void => {
    setPipelineWizContext(pipelineWizContext);
    setCurWizardStep(undefined, "settings");
  };

  const getTestResults = (): void => {
    setTestResults(false);

    pipelineService
      .testMapping(pipelineWizContext.values?.mapping)
      .then(({ data }) => {
        bannerNotificationService.success("Test successfully passed.");
        setTestResults(true);
      })
      .catch((err) => {
        setTestResults(false);
        if (
          err &&
          err.response &&
          err.response.data &&
          err.response.data.message
        ) {
          bannerNotificationService.error(err.response.data.message);
        }
      });
  };

  const formValidationSchema = yup.object().shape({
    primaryKeys: yup
      .array()
      .min(1, "Atleast one primary key should be selected"),
    method: yup.string().required("Method is required"),
    url: yup.string().url().required("URL is required"),
    // template: yup.string().required("Body cannot be empty"),
  });

  // const onFieldSelect = (event: ReactSelectOption) => {
  const onFieldSelect = (event: any) => {
    formInputUtils.insertTextInInput(
      templateareaRef,
      "{{" + event.value + "}}",
      templateValue,
      setTemplateValue
    );
  };

  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      <div>
        <Formik
          key={`${!!warehouseSchemaFields}`}
          initialValues={
            {
              type: PipelineMappingType.TARGET_REST_MAPPING,
              url: "",
              method: HttpMethod.POST,
              template: "",
              primaryKeys: [],
              fieldMappings: [],
              headers: {},
            } as PipelineMappingDto
          }
          validationSchema={formValidationSchema}
          onSubmit={(values, { setSubmitting }) => {
            values.template = templateValue;

            if (!pipelineWizContext.values) return setSubmitting(false);
            delete (values as any).variable;
            pipelineWizContext.values.mapping = values;

            if (!values.template) {
              bannerNotificationService.error("Body cannot be empty");
              return setSubmitting(false);
            }

            getTestResults();
          }}
          enableReinitialize
        >
          {({ values, setFieldValue, setFieldTouched, isSubmitting }) => (
            <Form>
              <InputSelect
                title="Primary Keys"
                name="primaryKeys"
                options={warehouseSchemaFields}
                deps={undefined}
                values={values}
                isMulti
                setFieldValue={setFieldValue}
                setFieldTouched={setFieldTouched}
                required
              />
              <Row>
                <Col sm={3}>
                  <InputSelect
                    title="Method"
                    name="method"
                    options={formInputUtils.getEnumSelectOptions(
                      RestApiMethodLabel
                    )}
                    deps={undefined}
                    values={values}
                    setFieldValue={setFieldValue}
                    setFieldTouched={setFieldTouched}
                    required
                  />
                </Col>
                <Col sm={9}>
                  <InputField
                    name="url"
                    type="text"
                    title="URL"
                    setFieldValue={setFieldValue}
                    setFieldTouched={setFieldTouched}
                    inputClassName="form-control-lg"
                    required
                  />
                </Col>
              </Row>
              <label>Headers</label>
              {headerKeys.map((headerKey, i) => (
                <Row>
                  <Col>
                    <input
                      placeholder="Key"
                      type="text"
                      className="form-control form-control-md"
                      defaultValue={headerKey}
                      onChange={(e) => {
                        headerKeys[i] = e.currentTarget.value;
                        headerKeys = _.compact(headerKeys);
                        headerKeys.push("");
                        setHeaderKeys(_.cloneDeep(headerKeys));
                      }}
                    />
                  </Col>
                  <Col>
                    <InputField
                      name={`headers[${headerKey+i}]`}
                      placeholder="Value"
                      type="text"
                      title={undefined}
                      values={values}
                      setFieldValue={setFieldValue}
                      setFieldTouched={setFieldTouched}
                      inputClassName="form-control-md"
                      required
                    />
                  </Col>
                </Row>
              ))}
              <label>
                <span className="required-icon">*</span>
                Mapping
              </label>
              <p className="text-muted mb-1">
                The body should be a Mustache template for mapping of fields
                from source to destination.{" "}
                <a href="https://mustache.github.io/mustache.5.html">
                  Read more about Mustache templating.
                </a>
              </p>
              <div className="mapping-wrapper">
                <Col className="col-md-9">
                  <TextareaAutosize
                    name="template"
                    ref={templateareaRef}
                    value={templateValue}
                    className="form-control"
                    minRows={9}
                    onChange={({ target }) => setTemplateValue(target.value)}
                  />
                </Col>

                <Col className="rh-xs-panel">
                  <Select
                    size="sm"
                    onChange={(e: any) => onFieldSelect(e)}
                    options={warehouseSchemaFields?.map((option) =>
                      toReactSelectOption(option)
                    )}
                  ></Select>

                  {/* <ListGroup>
                    {warehouseSchemaFields?.map((option, i) => (
                      <ListGroup.Item
                        action
                        onClick={() => onFieldSelect(option)}
                      >
                        {option.value}
                      </ListGroup.Item>
                    ))}
                  </ListGroup> */}
                </Col>
              </div>
              <Button
                type="submit"
                className="btn mt-2"
                variant="outline-primary"
              >
                Run test
              </Button>
              &nbsp;&nbsp;&nbsp;
              {testResults && (
                <Button
                  type="button"
                  className="btn btn-primary mt-2 ms-2"
                  onClick={nextStep}
                >
                  Continue
                </Button>
              )}
            </Form>
          )}
        </Formik>
      </div>
    </Layout>
  );
};

export default PipelineMappingRestApi;
