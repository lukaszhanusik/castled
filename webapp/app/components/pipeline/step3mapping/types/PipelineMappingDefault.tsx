import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import React from "react";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto2";
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
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import pipelineMappingUtils from "@/app/common/utils/pipelineMappingUtils";
import { PipelineMappingType } from "@/app/common/enums/PipelineMappingType";

interface MappingInfo {
  [warehouseKey: string]: {
    appField: string;
    isPrimaryKey: boolean;
  };
}

interface PipelineMappingDefaultProps extends PipelineWizardStepProps {
  pipelineSchema: PipelineSchemaResponseDto | undefined;
  isLoading: boolean;
}

const PipelineMappingDefault = ({
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
  pipelineSchema,
  isLoading,
}: PipelineMappingDefaultProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  if (!pipelineWizContext) {
    return <Loading />;
  }
  const appSchemaFields = pipelineMappingUtils.getSchemaFieldsAsOptions(
    pipelineSchema?.appSchema
  );
  const transformMapping = (mappingInfo: MappingInfo): PipelineMappingDto => {
    const fieldMappings: FieldMapping[] = [];
    const primaryKeys: string[] = [];
    _.each(mappingInfo, (value, key) => {
      if (value.appField) {
        fieldMappings.push({
          warehouseField: key,
          appField: value.appField,
          skipped: false,
        });
      }
      if (value.isPrimaryKey) {
        primaryKeys.push(value.appField);
      }
    });
    return {
      primaryKeys,
      fieldMappings,
    };
  };

  const initialMappingInfo: MappingInfo = (pipelineWizContext.mappingInfo || {
    type: PipelineMappingType.TARGET_FIELDS_MAPPING,
  }) as MappingInfo;
  if (!appSchemaFields) {
    pipelineSchema?.warehouseSchema.fields.map(
      (field) =>
        (initialMappingInfo[field.fieldName] = {
          appField: field.fieldName,
          isPrimaryKey: false,
        })
    );
  }

  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      <div className="table-responsive">
        <Formik
          initialValues={initialMappingInfo}
          onSubmit={(values, { setSubmitting }) => {
            if (!pipelineWizContext.values) return setSubmitting(false);
            pipelineWizContext.mappingInfo = values;
            pipelineWizContext.values.mapping = transformMapping(values);
            if (
              pipelineWizContext.values.mapping.primaryKeys?.length == 0 &&
              !pipelineSchema?.pkEligibles.autoDetect
            ) {
              setSubmitting(false);
              bannerNotificationService.error(
                "Atleast one primary key should be selected"
              );
              return;
            }

            pipelineWizContext.values.mapping.type =
              PipelineMappingType.TARGET_FIELDS_MAPPING;

            setPipelineWizContext(pipelineWizContext);
            setCurWizardStep(undefined, "settings");
            setSubmitting(false);
          }}
        >
          {({ values, setFieldValue, setFieldTouched, isSubmitting }) => (
            <Form>
              <Table hover>
                <thead>
                  <tr>
                    <th>Warehouse Column</th>
                    <th>App Column</th>
                    {!pipelineSchema?.pkEligibles.autoDetect && (
                      <th>Primary Key</th>
                    )}
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {pipelineSchema
                    ? pipelineSchema.warehouseSchema.fields.map((field, i) => (
                        <Placeholder as="tr" animation="glow" key={i}>
                          <Placeholder as="td">{field.fieldName}</Placeholder>
                          {appSchemaFields && (
                            <Placeholder as="td">
                              <InputSelect
                                title={undefined}
                                options={appSchemaFields}
                                deps={undefined}
                                values={values}
                                setFieldValue={setFieldValue}
                                setFieldTouched={setFieldTouched}
                                name={field.fieldName + ".appField"}
                              />
                            </Placeholder>
                          )}
                          {!appSchemaFields && (
                            <Placeholder as="td">
                              <InputField
                                type="text"
                                title={undefined}
                                values={values}
                                setFieldValue={setFieldValue}
                                setFieldTouched={setFieldTouched}
                                name={field.fieldName + ".appField"}
                              />
                            </Placeholder>
                          )}
                          <Placeholder
                            as="td"
                            className={classNames("my-auto", {
                              "d-none": pipelineSchema.pkEligibles.autoDetect,
                            })}
                          >
                            <InputCheckbox
                              title={undefined}
                              name={field.fieldName + ".isPrimaryKey"}
                              className="mt-3"
                              disabled={
                                pipelineSchema.pkEligibles.eligibles.length >
                                  0 &&
                                (_.get(
                                  values,
                                  field.fieldName + ".appField"
                                ) === undefined ||
                                  !pipelineSchema.pkEligibles.eligibles?.includes(
                                    _.get(values, field.fieldName).appField
                                  ))
                              }
                              defaultValue={false}
                            />
                          </Placeholder>
                          <Placeholder as="td">
                            <IconTrash
                              className={classNames({
                                "d-none":
                                  _.get(
                                    values,
                                    field.fieldName + ".appField"
                                  ) === undefined,
                              })}
                              onClick={() => {
                                setFieldValue(field.fieldName, "");
                              }}
                            ></IconTrash>
                          </Placeholder>
                        </Placeholder>
                      ))
                    : isLoading && (
                        <tr>
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
                      )}
                </tbody>
              </Table>
              <ButtonSubmit submitting={isSubmitting}>Continue</ButtonSubmit>
            </Form>
          )}
        </Formik>
      </div>
    </Layout>
  );
};

export default PipelineMappingDefault;

