export interface NewPipelineSchemaResponseDto {
  warehouseSchema: WarehouseSchema;
  mappingGroups:   MappingGroup[];
}

export interface MappingGroup {
  title:       string;
  description: string;
  type:        string;
  fields?:     MappingGroupField[];
}

export interface MappingGroupField {
  optional:    boolean;
  title:       string;
  description: string;
}

export interface WarehouseSchema {
  fields: WarehouseSchemaField[];
}

export interface WarehouseSchemaField {
  fieldName: string;
  type:      string;
  optional:  boolean;
}