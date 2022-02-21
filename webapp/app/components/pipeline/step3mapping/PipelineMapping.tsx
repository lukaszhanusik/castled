import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { Table } from "react-bootstrap";
import { IconTrash } from "@tabler/icons";
import InputSelect from "@/app/components/forminputs/InputSelect";
import InputField from "@/app/components/forminputs/InputField";
import _ from "lodash";
import { Form, Formik } from "formik";
// import InputCheckbox from "@/app/components/forminputs/InputCheckbox";
import Loading from "@/app/components/common/Loading";
// import classNames from "classnames";
// import {
//   FieldMapping,
//   PipelineMappingDto,
// } from "@/app/common/dtos/PipelineCreateRequestDto";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
// import Placeholder from "react-bootstrap/Placeholder";
// import Select from "react-select";
// import { MappingFieldsProps, SchemaOptions } from "./types/componentTypes";
// import MappingImportantFields from "./components/MappingImportantFields";
// import MappingMiscellaneousFields from "./components/MappingMiscellaneousFields";
// // import MappingMiscellaneousFields from "./components/MappingTableSelectOnlyBody";
// import WarehouseColumn from "./components/WarehouseColumn";
// import MappingTableSelectOnlyBody from "./components/MappingTableSelectOnlyBody";
import DynamicMappingFields from "./DynamicMappingFields";
import LoadingTable from "./components/LoadingTable";

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
        <LoadingTable />
      </Layout>
    );
  }

  console.log(pipelineSchema);

  // Tells the type of App selected. For e.g. Hubspot, Customer.io etc.
  const initialMappingInfo: MappingInfo = (pipelineWizContext.mappingInfo ||
    {}) as MappingInfo;

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
