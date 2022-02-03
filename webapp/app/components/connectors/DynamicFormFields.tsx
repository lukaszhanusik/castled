import { FormFieldMeta, FormFieldsDto } from "@/app/common/dtos/FormFieldsDto";
import InputCheckbox from "../forminputs/InputCheckbox";
import InputField from "../forminputs/InputField";
import InputFile from "../forminputs/InputFile";
import InputSelect from "@/app/components/forminputs/InputSelect";
import { AxiosResponse } from "axios";

import jexl from "jexl";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import _ from "lodash";
import { FormFieldType } from "@/app/common/enums/FormFieldType";
import { FieldMetaProps } from "formik";
import dynamicFormUtils from "@/app/common/utils/dynamicFormUtils";

export interface DynamicFormFieldsProps {
  namePrefix?: string;
  formFields?: FormFieldsDto;
  skipNames?: string[];
  values: any;
  setFieldValue?: (field: string, value: any, shouldValidate?: boolean) => void;
  setFieldTouched?: (
    field: string,
    isTouched?: boolean,
    shouldValidate?: boolean
  ) => void;
  setFieldError?: (field: string, message: string | undefined) => void;
  dataFetcher?: (
    optionsRef: string
  ) => Promise<AxiosResponse<DataFetcherResponseDto>>;
}

interface OrderedFieldInfo {
  order: number;
  key: string;
  group: string;
}

const fieldRenderers: {
  [key in FormFieldType]: { renderer: any; props?: { [type: string]: any } };
} = {
  TEXT_BOX: { renderer: InputField, props: { type: "text" } },
  CHECK_BOX: { renderer: InputCheckbox },
  RADIO_GROUP: { renderer: InputSelect },
  DROP_DOWN: { renderer: InputSelect },
  JSON_FILE: { renderer: InputFile, props: { type: "json" } },
  TEXT_FILE: { renderer: InputFile, props: { type: "text" } },
  HIDDEN: { renderer: InputSelect, props: { hidden: true } },
};

const DynamicFormFields = ({
  namePrefix,
  formFields,
  skipNames,
  values,
  setFieldValue,
  dataFetcher,
}: DynamicFormFieldsProps) => {
  if (!formFields?.fields) return null;
  const fields: Array<any> = [];
  const skipNamesSet = new Set<String>();
  if (skipNames) {
    skipNames.forEach((name) => skipNamesSet.add(name));
  }
  // Handle ordering
  const orderedFieldsInfo: OrderedFieldInfo[] = [];
  const names = Object.keys(formFields.fields);

  names.forEach((key, i) => {
    const group = formFields.fields[key].group;
    orderedFieldsInfo.push({ order: i, key, group });
  });
  // Display
  for (const fieldInfo of orderedFieldsInfo) {
    const key = fieldInfo.key;
    if (skipNamesSet.has(key)) continue;
    const field: FormFieldMeta = formFields.fields[key];
    const fieldRenderer = fieldRenderers[field.fieldProps.type];
    if (!fieldRenderer) {
      console.error("Field renderer not found for " + field);
      continue;
    }
    const depValues: any[] = [];
    // Skip if group activator is present but dependency not met
    if (field.group in formFields.groupActivators) {
      const groupActivator = formFields.groupActivators[field.group];
      const skip = dynamicFormUtils.isFieldHidden(
        groupActivator,
        namePrefix,
        values,
        depValues
      );
      if (skip) continue;
    }
    const { renderer: Input, props } = fieldRenderer;
    const name = namePrefix ? `${namePrefix}.${key}` : key;
    fields.push(
      <Input
        key={name}
        required={field.validations.required}
        name={name}
        {...field.fieldProps}
        {...props}
        defaultValue=""
        dValues={depValues}
        values={values}
        dataFetcher={dataFetcher}
        setFieldValue={setFieldValue}
        deps={formFields.groupActivators[field.group]?.dependencies}
        title={field.fieldProps.title || key}
      />
    );
  }
  return <>{fields}</>;
};

export default DynamicFormFields;
