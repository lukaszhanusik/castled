import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseRestApiDto } from "@/app/common/dtos/PipelineSchemaResponseDto2";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import _ from "lodash";
import Loading from "@/app/components/loaders/Loading";
import PipelineMappingRestApi from "./types/PipelineMappingRestApi";

const PipelineMappingRestWizard = ({
  appBaseUrl,
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
}: PipelineWizardStepProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  const [pipelineSchema, setPipelineSchema] = useState<
    PipelineSchemaResponseRestApiDto | undefined
  >();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  useEffect(() => {
    if (!pipelineWizContext) return;
    if (!pipelineWizContext.values) {
      setCurWizardStep("source", "selectType");
      return;
    }
    pipelineService
      .getSchemaForRestMapping(pipelineWizContext.values)
      .then(({ data }) => {
        setIsLoading(false);
        setPipelineSchema(data);
      })
      .catch(() => {
        setIsLoading(false);
        bannerNotificationService.error("Unable to load schemas");
      });
  }, [pipelineWizContext?.values]);
  if (!pipelineWizContext) {
    return <Loading />;
  }
  return (
    <PipelineMappingRestApi
      appBaseUrl={appBaseUrl}
      curWizardStep={curWizardStep}
      steps={steps}
      stepGroups={stepGroups}
      setCurWizardStep={setCurWizardStep}
      pipelineSchema={pipelineSchema}
      isLoading={isLoading}
    />
  );
};

export default PipelineMappingRestWizard;
