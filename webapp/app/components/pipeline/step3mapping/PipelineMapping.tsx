import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import {
  PipelineSchemaResponseDto,
  PrimaryKeyElement,
} from "@/app/common/dtos/PipelineSchemaResponseDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { Table } from "react-bootstrap";
import { IconTrash } from "@tabler/icons";
import InputSelect from "@/app/components/forminputs/InputSelect";
import InputField from "@/app/components/forminputs/InputField";
import _ from "lodash";
import { Form, Formik } from "formik";
import InputCheckbox from "@/app/components/forminputs/InputCheckbox";
import Loading from "@/app/components/common/Loading";
import classNames from "classnames";
import {
  FieldMapping,
  PipelineMappingDto,
} from "@/app/common/dtos/PipelineCreateRequestDto";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import Placeholder from "react-bootstrap/Placeholder";
import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "./types/componentTypes";
import MappingImportantFields from "./components/MappingImportantFields";
import MappingMiscellaneousFields from "./components/MappingMiscellaneousFields";
// import MappingMiscellaneousFields from "./components/MappingTableSelectOnlyBody";
import WarehouseColumn from "./components/WarehouseColumn";
import MappingTableSelectOnlyBody from "./components/MappingTableSelectOnlyBody";
import DynamicMappingFields from "./DynamicMappingFields";

interface MappingInfo {
  [warehouseKey: string]: {
    appField: string;
    isPrimaryKey: boolean;
  };
}

const PipelineMapping = ({
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
}: PipelineWizardStepProps) => {
  // State management using context, which depends on sessionStorage.
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  const [pipelineSchema, setPipelineSchema] = useState<
    PipelineSchemaResponseDto | undefined
  >();
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Lifecycle based on getting schema from backend to populate items
  useEffect(() => {
    if (!pipelineWizContext) return;
    if (!pipelineWizContext.values) {
      setCurWizardStep("source", "selectType");
      return;
    }
    /* 
    Getting schema from the backend API call. 
    See PipelineSchemaResponseDto for the expected response.
    */
    pipelineService
      .getSchemaForMapping(pipelineWizContext.values)
      .then(({ data }) => {
        setIsLoading(false);
        setPipelineSchema(data);
      })
      .catch(() => {
        setIsLoading(false);
        bannerNotificationService.error("Unable to load schemas");
      });
  }, [pipelineWizContext?.values]);

  if (!pipelineWizContext) {
    return <Loading />;
  }

  if (!pipelineSchema) {
    return (
      <Layout
        title={steps[curWizardStep].title}
        subTitle={steps[curWizardStep].description}
        centerTitle={true}
        steps={steps}
        stepGroups={stepGroups}
      >
        <div>
          <div className="table-responsive mx-auto mt-2">
            <Table hover>
              <tbody>
                <tr className="pt-4 pb-4">
                  <td>
                    <div className="linear-background"></div>
                  </td>
                  <td>
                    <div className="linear-background"></div>
                  </td>
                  <td>
                    <div className="linear-background"></div>
                  </td>
                  <td>
                    <div className="linear-background"></div>
                  </td>
                </tr>
              </tbody>
            </Table>
          </div>
        </div>
      </Layout>
    );
  }

  console.log(pipelineSchema);

  const appSchemaOptions = pipelineSchema?.warehouseSchema?.fields.map(
    (field) => ({
      value: field.fieldName,
      label: field.fieldName,
    })
  );

  const { warehouseSchema, mappingGroups } = pipelineSchema;

  // Tells the type of App selected. For e.g. Hubspot, Customer.io etc.
  const initialMappingInfo: MappingInfo = (pipelineWizContext.mappingInfo ||
    {}) as MappingInfo;

  // SECTION - 1 - Mandatory fields filter from warehouseSchema
  const importantParamsSection = mappingGroups.filter((fields) => {
    return fields.type === "IMPORTANT_PARAMS" && fields.fields;
  });

  // SECTION - 2 - Primary Keys to match the destination object
  const primaryKeysSection = mappingGroups.filter((fields) => {
    return fields.type === "PRIMARY_KEYS" && fields;
  });

  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function appSchemaPrimaryKeysFilter(option: PrimaryKeyElement) {
    return [{ value: option.fieldName, label: option.fieldName }];
  }

  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      <div className="container">
        <Formik
          initialValues={initialMappingInfo}
          onSubmit={(values, { setSubmitting }) => {
            if (!pipelineWizContext.values) return setSubmitting(false);
            pipelineWizContext.mappingInfo = values;
            setPipelineWizContext(pipelineWizContext);
            setCurWizardStep(undefined, "settings");
            setSubmitting(false);
          }}
        >
          {({ values, setFieldValue, setFieldTouched, isSubmitting }) => (
            <Form className="container">
              <DynamicMappingFields
                values={values}
                formFields={pipelineSchema}
                setFieldValue={setFieldValue}
              />
              <ButtonSubmit submitting={isSubmitting}>
                TEST & CONTINUE
              </ButtonSubmit>
            </Form>
          )}
        </Formik>
      </div>
    </Layout>
  );
};

export default PipelineMapping;
