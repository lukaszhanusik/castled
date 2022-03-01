export interface PipelineSchemaResponseDto {
  warehouseSchema: WarehouseSchema;
  mappingGroups: MappingGroup[];
}

export interface MappingGroup {
  title: string;
  description: string;
  type: string;
  primaryKeys?: FieldElement[];
  mandatoryFields?: FieldElement[];
  optionalFields?: FieldElement[];
  fields?: ImportantParamsField[];
}

export interface ImportantParamsField {
  fieldName: string;
  type: Type;
  optional: boolean;
  title: string;
  description: string;
  fieldDisplayName?: string;
}

export enum Type {
  Long = "Long",
  String = "String",
}

export interface FieldElement {
  fieldName: string;
  type: Type;
  optional: boolean;
  fieldDisplayName?: string;
}
export interface PrimaryKeyElement {
  fieldName: string;
  type: string;
  optional: boolean;
  fieldDisplayName?: string;
}
export interface WarehouseSchema {
  fields: FieldElement[];
}

// export type ConnectorSchema = any;
