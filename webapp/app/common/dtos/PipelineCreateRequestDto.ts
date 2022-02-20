import { StringAnyMap } from "./../utils/types";
import { ScheduleType } from "@/app/common/enums/ScheduleType";
import { QueryMode } from "../enums/QueryMode";
import { PipelineMappingType } from "../enums/PipelineMappingType";
import { HttpMethod } from "../enums/HttpMethod";

export interface PipelineCreateRequestDto {
  name?: string;
  schedule?: PipelineSchedule;
  appId?: number;
  warehouseId?: number;
  sourceQuery?: string;
  queryMode?: QueryMode;
  appSyncConfig: {
    appType?: string;
    [key: string]: string | undefined;
  };
  mapping?: PipelineMappingDto;
}

export interface PipelineMappingDto {
  primaryKeys?: string[];
  type?: PipelineMappingType;
  fieldMappings?: FieldMapping[];
  url?: string;
  method?: HttpMethod;
  template?: string;
  headers?: StringAnyMap;
}

export interface FieldMapping {
  warehouseField?: string;
  appField?: string;
  skipped?: boolean;
}

export interface PipelineSchedule {
  type?: ScheduleType;
  cronExpression?: string;
  frequency?: number;
}
