export interface ConnectorSchema {
  schemaName: string;
  fields: {
    fieldName: string;
    type: string;
    optional: true;
  }[];
}

export interface PipelineSchemaResponseDto {
  warehouseSchema: ConnectorSchema;
  appSchema: ConnectorSchema;
  pkEligibles: {
    eligibles : string[],
    autoDetect : boolean
  }
}
