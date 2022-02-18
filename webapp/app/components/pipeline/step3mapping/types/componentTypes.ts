export interface MappingFieldsProps {
  title: string | undefined;
  description?: string;
  options: SchemaOptions[];
  setFieldValue: (field: string, value: any, shouldValidate?: boolean) => void
  setFieldTouched: (value: any) => void;
  fieldName: string,
}

export interface SchemaOptions {
  value: any;
  title: string;
  description?: string;
}