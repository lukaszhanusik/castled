export interface ModelResponseDto {
  id:                number;
  teamId:            number;
  warehouse:         Warehouse;
  modelName:         string;
  modelType:         string;
  modelDetails:      ModelDetails;
  queryModelPK:      QueryModelPK;
  activeSyncDetails: ActiveSyncDetail[];
  activeSyncsCount:  number;
  demo:              boolean;
}

export interface ActiveSyncDetail {
  id:             number;
  seqId:          number;
  teamId:         number;
  uuid:           string;
  name:           string;
  jobSchedule:    JobSchedule;
  sourceQuery:    string;
  status:         string;
  syncStatus:     string;
  appSyncConfig:  AppSyncConfig;
  dataMapping:    DataMapping;
  app:            Warehouse;
  queryMode:      string;
  warehouse:      Warehouse;
  lastRunDetails: LastRunDetails;
  deleted:        boolean;
}

export interface Warehouse {
  id:             number;
  type:           string;
  name:           string;
  logoUrl:        string;
  columnDetails?: ColumnDetail[];
}

export interface ColumnDetail {
  name:   string;
  schema: Schema;
}

export interface Schema {
  type:         string;
  allowedTypes: string[];
  optional:     boolean;
  maxLength?:   number;
}

export interface AppSyncConfig {
  appType: string;
  object:  Object;
  mode:    string;
}

export interface Object {
  objectName: string;
}

export interface DataMapping {
  primaryKeys:   string[];
  type:          string;
  fieldMappings: FieldMapping[];
}

export interface FieldMapping {
  warehouseField: string;
  appField:       string;
  skipped:        boolean;
}

export interface JobSchedule {
  type:      string;
  frequency: number;
}

export interface LastRunDetails {
  lastRuns:  LastRun[];
  lastRunTs: number;
}

export interface LastRun {
  id:                number;
  status:            string;
  stage:             string;
  pipelineId:        number;
  pipelineSyncStats: PipelineSyncStats;
  processedTs:       number;
  createdTs:         number;
}

export interface PipelineSyncStats {
  recordsSynced:  number;
  recordsFailed:  number;
  recordsSkipped: number;
  offset:         number;
}

export interface ModelDetails {
  sourceQuery: string;
}

export interface QueryModelPK {
  primaryKeys: string[];
}
