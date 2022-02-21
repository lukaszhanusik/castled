import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import {
  PipelineSchemaResponseDto,
} from "@/app/common/dtos/PipelineSchemaResponseDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import _ from "lodash";
<<<<<<< HEAD
import Loading from "@/app/components/common/Loading";
<<<<<<< HEAD
import classNames from "classnames";
import {
  FieldMapping,
  PipelineMappingDto,
} from "@/app/common/dtos/PipelineCreateRequestDto";
=======
import { Form, Formik } from "formik";
// import InputCheckbox from "@/app/components/forminputs/InputCheckbox";
import Loading from "@/app/components/common/Loading";
// import classNames from "classnames";
// import {
//   FieldMapping,
//   PipelineMappingDto,
// } from "@/app/common/dtos/PipelineCreateRequestDto";
>>>>>>> 24cd59e2b4cc8a8ef29e80062bd1ed3c684f6797
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
=======
import PipelineMappingDefault from "./types/PipelineMappingDefault";
import PipelineMappingRestApi from "./types/PipelineMappingRestApi";
>>>>>>> db16da0c148b2c0fed4cffe07107bd5929f5f7d8

const PipelineMapping = ({
  appBaseUrl,
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
<<<<<<< HEAD

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

<<<<<<< HEAD
  // SECTION - 1 - Mandatory fields filter from warehouseSchema
  const sectionOneFields = mappingGroups.filter((fields) => {
    return fields.type === "IMPORTANT_PARAMS" && fields.fields;
  });

  // SECTION - 2 - Primary Keys to match the destination object
  const sectionTwoFields = mappingGroups.filter((fields) => {
    return fields.type === "PRIMARY_KEYS" && fields;
  });

  // SECTION - 3 - Other fields to match the destination object
  const sectionThreeFields = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const sectionFourFields = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function appSchemaPrimaryKeysFilter(option: PrimaryKeyElement) {
    return [{ value: option.fieldName, label: option.fieldName }];
=======
  if (pipelineWizContext.appType?.value === "RESTAPI") {
    return (
      <PipelineMappingRestApi
        appBaseUrl={appBaseUrl}
        curWizardStep={curWizardStep}
        steps={steps}
        stepGroups={stepGroups}
        setCurWizardStep={setCurWizardStep}
        pipelineSchema={pipelineSchema}
        isLoading={isLoading}
      />
    );
>>>>>>> db16da0c148b2c0fed4cffe07107bd5929f5f7d8
  }
=======
>>>>>>> 24cd59e2b4cc8a8ef29e80062bd1ed3c684f6797
  return (
    <PipelineMappingDefault
      appBaseUrl={appBaseUrl}
      curWizardStep={curWizardStep}
      steps={steps}
      stepGroups={stepGroups}
<<<<<<< HEAD
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
=======
      setCurWizardStep={setCurWizardStep}
      pipelineSchema={pipelineSchema}
      isLoading={isLoading}
    />
>>>>>>> db16da0c148b2c0fed4cffe07107bd5929f5f7d8
  );
};

export default PipelineMapping;
