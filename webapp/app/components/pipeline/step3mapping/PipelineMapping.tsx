import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import React, { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import _ from "lodash";
import Loading from "@/app/components/common/Loading";
import PipelineMappingDefault from "./types/PipelineMappingDefault";
import PipelineMappingRestApi from "./types/PipelineMappingRestApi";

const PipelineMapping = ({
  appBaseUrl,
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
}: PipelineWizardStepProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  const [pipelineSchema, setPipelineSchema] = useState<
    PipelineSchemaResponseDto | undefined
  >();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  useEffect(() => {
    if (!pipelineWizContext) return;
    if (!pipelineWizContext.values) {
      setCurWizardStep("source", "selectType");
      return;
    }
    pipelineService
      .getSchemaForMapping(pipelineWizContext.values)
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
  if (pipelineWizContext.appType?.value === "RESTAPI") {
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
  }
  return (
    <PipelineMappingDefault
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

export default PipelineMapping;
