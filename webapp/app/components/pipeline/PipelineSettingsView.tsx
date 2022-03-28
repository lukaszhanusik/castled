import { PipelineSchedule } from "@/app/common/dtos/PipelineCreateRequestDto";
import React from "react";
import PipelineSettingsForm, {
  PipelineSettingsConfig,
} from "./PipelineSettingsForm";

import { SettingSchedule } from "@/app/components/pipeline/PipelineSettingsForm";
import bannerNotificationService from "@/app/services/bannerNotificationService";

import pipelineService from "@/app/services/pipelineService";
import { ScheduleTimeUnit } from "@/app/common/enums/ScheduleType";
import { QueryMode } from "@/app/common/enums/QueryMode";
import pipelineScheduleUtils from "@/app/common/utils/pipelineScheduleUtils";

const defaultPipelineSettings = {
  queryMode: QueryMode.INCREMENTAL,
  schedule: { frequency: 60, timeUnit: ScheduleTimeUnit.MINUTES },
} as PipelineSettingsConfig;

export interface PipelineSettingsViewProps {
  pipelineId?: number;
  name?: string;
  schedule?: PipelineSchedule;
  queryMode?: QueryMode;
}
function PipelineSettingsView({
  pipelineId,
  name,
  schedule,
  queryMode,
}: PipelineSettingsViewProps) {
  const handleSettingsUpdate = (
    name: string,
    pipelineSchedule: PipelineSchedule,
    queryMode?: QueryMode,
  ) => {
    pipelineService.updatePipeline(pipelineId!, {
      name: name,
      schedule: pipelineSchedule,
      queryMode: queryMode!,
    }).then(() => bannerNotificationService.success("Pipeline Updated"))
  };
  return (
    <PipelineSettingsForm
      initialValues={
        {
          name: name,
          queryMode: queryMode,
          schedule: pipelineScheduleUtils.getSettingsSchedule(schedule),
        } as PipelineSettingsConfig
      }
      onSubmit={handleSettingsUpdate}
      submitLabel="Update"
    ></PipelineSettingsForm>
  );
}

export default PipelineSettingsView;
