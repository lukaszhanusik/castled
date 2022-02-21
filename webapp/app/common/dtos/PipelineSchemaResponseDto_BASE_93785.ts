export interface PipelineSchemaResponseDto {
  warehouseSchema: WarehouseSchema;
  mappingGroups:   MappingGroup[];
}

export interface MappingGroup {
  title:        string;
  description:  string;
  type:         string;
  primaryKeys?: PrimaryKeyElement[];
  fields?:      ImportantParamsField[];
}

export interface ImportantParamsField {
  optional: boolean;
  title: string;
  description: string;
  type: string;
  fieldName: string;
}

export interface PrimaryKeyElement {
  fieldName: string;
  type:      string;
  optional:  boolean;
}

export interface WarehouseSchema {
  fields: PrimaryKeyElement[];
}

// export interface PipelineSchemaResponseDto {
//   warehouseSchema: {
//     schemaName: string;
//     fields: {
//       fieldName: string;
//       type: string;
//       optional: true;
//     }[];
//   };
//   appSchema: {
//     schemaName: string;
//     fields: {
//       fieldName: string;
//       type: string;
//       optional: true;
//     }[];
//   };
//   pkEligibles: {
//     eligibles : string[],
//     autoDetect : boolean
//   }
// }
