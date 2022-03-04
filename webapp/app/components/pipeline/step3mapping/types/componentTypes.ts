import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { FocusEventHandler } from "react-select";

export interface MappingFieldsProps {
  options: SchemaOptions[];
  mappingGroups: MappingGroup[];
  values?: any;
  setFieldValue?: (field: string, value: any, shouldValidate?: boolean) => void;
  setFieldTouched?: (
    field: string,
    isTouched?: boolean,
    shouldValidate?: boolean
  ) => void;
  setFieldError?: (field: string, value: string | undefined) => void;
  errors: any;
  appType?: string;
}

export interface SchemaOptions {
  value: any;
  label: string;
}

export interface DestinationFieldRowsProps {
  options: SchemaOptions[];
  destinationFieldSection?: MappingGroup[];
  defaultWarehouseValue?: { value: string; label: string };
  defaultAppValue?: { value: string; label: string };
  isDisabled?: boolean;
  onChange?: (value: any) => void;
  onChangeWarehouse?: (value: any) => void;
  onChangeAppField?: (value: any) => void;
  handleDelete?: (value: any) => void;
  onBlur?: FocusEventHandler | undefined;
  values?: any;
  isClearable?: boolean;
}
