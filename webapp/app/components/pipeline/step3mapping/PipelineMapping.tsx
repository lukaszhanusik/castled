import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import _ from "lodash";
import { Form, Formik } from "formik";
import Loading from "@/app/components/common/Loading";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import DynamicMappingFields from "./DynamicMappingFields";
import LoadingTable from "./components/Layouts/LoadingTable";
import transformMapping from "./utils/transformMapping";
import { PipelineMappingType } from "@/app/common/enums/PipelineMappingType";
import mappingFieldValidations from "./utils/mappingFieldValidations";

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

  console.log(pipelineSchema);

  function validate(values: any) {
    const errors = {};
    const validationResult = pipelineSchema
      ? mappingFieldValidations(values, pipelineSchema.mappingGroups)
      : mappingFieldValidations(values);

    if (validationResult.length) {
      for (let error of validationResult) {
        Object.assign(errors, error);
      }
    }
    console.log(values);
    console.log(validationResult);
    return errors;
  }

  // Tells the type of App selected. For e.g. Hubspot, Customer.io etc.
  // const initialMappingInfo: MappingInfo = (pipelineWizContext.mappingInfo ||
  //   {}) as MappingInfo;

  const initialValuesForValidation: any = {
    appFieldRepeating: "",
    fillBothPrimaryFields: "",
  };

  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      {pipelineSchema ? (
        <div className="container">
          <Formik
            initialValues={initialValuesForValidation}
            validate={validate}
            validateOnChange={false}
            validateOnBlur={false}
            onSubmit={(values, { setSubmitting }) => {
              if (!pipelineWizContext.values) return setSubmitting(false);
              // pipelineWizContext.mappingInfo = values;
              // pipelineWizContext.values.mapping = transformMapping(values);
              console.log(values);
              // console.log(transformMapping(values));

              // pipelineWizContext.values.mapping.type =
              //   PipelineMappingType.TARGET_FIELDS_MAPPING;
              // setPipelineWizContext(pipelineWizContext);
              // setCurWizardStep(undefined, "settings");
              // setSubmitting(false);
            }}
          >
            {({
              values,
              setFieldValue,
              setFieldTouched,
              setFieldError,
              isSubmitting,
              errors,
            }) => (
              <Form className="container">
                <DynamicMappingFields
                  values={values}
                  mappingFields={pipelineSchema}
                  setFieldValue={setFieldValue}
                  setFieldTouched={setFieldTouched}
                  setFieldError={setFieldError}
                />
                {errors && (
                  <>
                    <span className="error">{errors.appFieldRepeating}</span>
                    <span className="error">
                      {errors.fillBothPrimaryFields}
                    </span>
                  </>
                )}
                <ButtonSubmit submitting={isSubmitting}>
                  TEST & CONTINUE
                </ButtonSubmit>
              </Form>
            )}
          </Formik>
        </div>
      ) : (
        <LoadingTable />
      )}
    </Layout>
  );
};

export default PipelineMapping;
