export interface ConnectorSchema {
  schemaName: string;
  fields: {
    fieldName: string;
    type: string;
    optional: true;
  }[];
}

export interface PipelineSchemaResponseRestApiDto {
  warehouseSchema: ConnectorSchema;
  appSchema: ConnectorSchema;
  pkEligibles: {
    eligibles: string[];
    autoDetect: boolean;
  };
}
