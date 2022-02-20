import { PipelineSchedule } from "@/app/common/dtos/PipelineCreateRequestDto";
import { QueryMode } from "../enums/QueryMode";

export interface PipelineUpdateRequestDto {
  name: string;
  schedule: PipelineSchedule;
  queryMode: QueryMode;
}
