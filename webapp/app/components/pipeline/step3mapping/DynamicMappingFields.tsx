import { FormFieldMeta, FormFieldsDto } from "@/app/common/dtos/FormFieldsDto";
import InputCheckbox from "../../forminputs/InputCheckbox";
import InputField from "../../forminputs/InputField";
import InputFile from "../../forminputs/InputFile";
import InputSelect from "@/app/components/forminputs/InputSelect";
import { AxiosResponse } from "axios";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import _ from "lodash";
import { FormFieldType } from "@/app/common/enums/FormFieldType";
import dynamicFormUtils from "@/app/common/utils/dynamicFormUtils";
import { MappingGroup, PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { MappingFieldEnums } from "./types/MappingFieldEnums";
import MappingImportantFields from "./components/MappingImportantFields";
import MappingMiscellaneousFields from "./components/MappingMiscellaneousFields";
import MappingTableSelectOnlyBody from "./components/MappingTableSelectOnlyBody";

export interface DynamicMappingFieldsProps {
  namePrefix?: string;
  formFields?: PipelineSchemaResponseDto;
  skipNames?: string[];
  values: any;
  setFieldValue?: (field: string, value: any, shouldValidate?: boolean) => void;
  setFieldTouched?: (
    field: string,
    isTouched?: boolean,
    shouldValidate?: boolean
  ) => void;
  setFieldError?: (field: string, message: string | undefined) => void;
  // dataFetcher?: (
  //   optionsRef: string
  // ) => Promise<AxiosResponse<DataFetcherResponseDto>>;
}

// interface OrderedFieldInfo {
//   order: number;
//   key: string;
//   group: string;
// }

const fieldRenderers: {
  [key: string]: {
    renderer: any;
  };
} = {
  IMPORTANT_PARAMS: { renderer: MappingImportantFields },
  PRIMARY_KEYS: { renderer: MappingTableSelectOnlyBody },
  DESTINATION_FIELDS: { renderer: MappingTableSelectOnlyBody },
  MISCELLANEOUS_FIELDS: { renderer: MappingMiscellaneousFields },
};

const DynamicMappingFields = ({
  // namePrefix,
  formFields,
  // skipNames,
  values,
  setFieldValue,
}: // dataFetcher,
DynamicMappingFieldsProps) => {
  if (!formFields?.warehouseSchema.fields || !formFields?.mappingGroups.length)
    return null;

  const { warehouseSchema, mappingGroups } = formFields;

  const fields: Array<any> = [];
  // const skipNamesSet = new Set<String>();

  // if (skipNames) {
  //   skipNames.forEach((name) => skipNamesSet.add(name));
  // }
  // // Handle ordering
  // const orderedFieldsInfo: OrderedFieldInfo[] = [];
  // const names = Object.keys(formFields.mappingGroups);

  // names.forEach((key, i) => {
  //   const group = formFields.fields[key].group;
  //   orderedFieldsInfo.push({ order: i, key, group });
  // });

  // Display
  for (const fieldInfo of mappingGroups) {
    // const index = fieldInfo;

    // if (skipNamesSet.has(key)) continue;
    // const field: MappingGroup = formFields.fields[key];

    const fieldRenderer = fieldRenderers[fieldInfo.type];

    if (!fieldRenderer) {
      console.error("Field renderer not found for type " + fieldInfo.type);
      continue;
    }

    // const depValues: any[] = [];
    // // Skip if group activator is present but dependency not met
    // if (field.group in formFields.groupActivators) {
    //   const groupActivator = formFields.groupActivators[field.group];
    //   const skip = dynamicFormUtils.isFieldHidden(
    //     groupActivator,
    //     namePrefix,
    //     values,
    //     depValues
    //   );
    //   if (skip) continue;
    // }
    const { renderer: Input } = fieldRenderer;

    const name = `mapping.${fieldInfo.type}`;

    fields.push(
      <Input
        key={name}
        // required={field.validations.required}
        name={name}
        // {...field.fieldProps}
        // {...props}
        defaultValue=""
        // dValues={depValues}
        values={values}
        // dataFetcher={dataFetcher}
        setFieldValue={setFieldValue}
        // deps={formFields.groupActivators[field.group]?.dependencies}
        // title={field.fieldProps.title || key}
      />
    );
  }
  return <>{fields}</>;
};

export default DynamicMappingFields;
