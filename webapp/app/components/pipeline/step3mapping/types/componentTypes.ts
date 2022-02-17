export interface MappingFieldsProps {
  title: string;
  description?: string;
  options: SchemaOptions[];
}

export interface SchemaOptions {
  value: string;
  label: string;
}