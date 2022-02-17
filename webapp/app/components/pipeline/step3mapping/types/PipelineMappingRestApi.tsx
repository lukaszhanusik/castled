import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React, { useEffect, useRef, useState } from "react";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { Col, Row, Table, Form } from "react-bootstrap";
import InputSelect from "@/app/components/forminputs/InputSelect";
import InputField from "@/app/components/forminputs/InputField";
import _ from "lodash";
import { Formik } from "formik";
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
  const [headerKeys, setHeaderKeys] = useState<string[]>([""]);
  const { isOss } = useSession();
  if (!pipelineWizContext) {
    return <Loading />;
  }
  const warehouseSchemaFields = pipelineMappingUtils.getSchemaFieldsAsOptions(
    pipelineSchema?.warehouseSchema
  );

  const toReactSelectOption = (o: SelectOptionDto): ReactSelectOption => ({
    value: o.value,
    label: o.title,
  });

  const formValidationSchema = yup.object().shape({
    primaryKeys: yup
      .array()
      .min(1, "Atleast one primary key should be selected"),
    method: yup.string().required("Method is required"),
    url: yup.string().url().required("URL is required"),
    template: yup.string().required("Body cannot be empty"),
  });

  let templateareaRef = useRef<HTMLTextAreaElement>(null);
  const [templateValue, setTemplateValue] = useState("");

  const onFieldSelect = (event: ReactSelectOption) => {
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
            console.log(values);
            if (!pipelineWizContext.values) return setSubmitting(false);
            delete (values as any).variable;
            pipelineWizContext.values.mapping = values;

            setPipelineWizContext(pipelineWizContext);
            setCurWizardStep(undefined, "settings");
            setSubmitting(false);
          }}
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
                        if (
                          e.currentTarget.value &&
                          i === headerKeys.length - 1
                        ) {
                          headerKeys.push("");
                        }
                        setHeaderKeys(_.cloneDeep(headerKeys));
                      }}
                    />
                  </Col>
                  <Col>
                    <InputField
                      name={`headers[${headerKey}]`}
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

              <Select
                size="sm"
                onChange={(e: any) => onFieldSelect(e)}
                options={warehouseSchemaFields?.map((option) =>
                  toReactSelectOption(option)
                )}
              ></Select>

              {/* <label>Body</label>
              <TextareaAutosize
                name="template"
                ref={templateareaRef}
                value={templateValue}
                className="form-control"
                minRows={6}
                onChange={({ target }) => setTemplateValue(target.value)}
              /> */}

              <InputField
                name="template"
                type="textarea"
                title="Body"
                setFieldValue={setFieldValue}
                setFieldTouched={setFieldTouched}
                minRows={6}
                required
                ref={templateareaRef}
                onChange={setTemplateValue}
                value={templateValue}
              />
              <ButtonSubmit submitting={isSubmitting}>Continue</ButtonSubmit>
            </Form>
          )}
        </Formik>
      </div>
    </Layout>
  );
};

export default PipelineMappingRestApi;
