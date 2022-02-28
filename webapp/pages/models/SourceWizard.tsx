import React from "react";
import ConnectorWizard from "@/app/components/connectors/ConnectorWizard";
import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import _ from "lodash";
import { ConnectorTypeDto } from "@/app/common/dtos/ConnectorTypeDto";
import Loading from "@/app/components/common/Loading";
import ModelType from "./ModelType";
import CreateModel from "./CreateModel";

const CUR_WIZARD_STEP_GROUP = "source";

const SourceWizard = ({
  appBaseUrl,
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
  onFinish,
}: PipelineWizardStepProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  if (!pipelineWizContext) return <Loading />;
  return (
    <>
      {curWizardStep !== "configureModel" && (
        <ConnectorWizard
          appBaseUrl={appBaseUrl}
          category={"Model"}
          curWizardStepGroup={CUR_WIZARD_STEP_GROUP}
          curWizardStep={curWizardStep}
          steps={steps}
          stepGroups={stepGroups}
          setCurWizardStep={setCurWizardStep}
          onConnectorTypeSelect={(type: ConnectorTypeDto) => {
            _.set(pipelineWizContext, "warehouseType", type);
            setPipelineWizContext(pipelineWizContext);
          }}
          onFinish={(id) => {
            console.log("--here---");
            _.set(pipelineWizContext, "values.warehouseId", id);
            setPipelineWizContext(pipelineWizContext);
            setCurWizardStep(CUR_WIZARD_STEP_GROUP, "configureModel");
          }}
        />
      )}

      {curWizardStep === "configureModel" && (
        <CreateModel
          appBaseUrl={appBaseUrl}
          curWizardStep={curWizardStep}
          stepGroups={steps}
          steps={{
            configureModel: {
              title: "Create Model",
              description: "",
            },
          }}
          setCurWizardStep={setCurWizardStep}
          onFinish={onFinish}
        />
      )}
    </>
  );
};

export default SourceWizard;
