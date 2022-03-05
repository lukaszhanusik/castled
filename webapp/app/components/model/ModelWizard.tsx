import React, { useEffect } from "react";
import { WizardSteps } from "@/app/common/dtos/internal/WizardSteps";
import { useRouter } from "next/router";
import wizardUtils from "@/app/common/utils/wizardUtils";
import Loading from "@/app/components/common/Loading";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import routerUtils from "@/app/common/utils/routerUtils";
import _ from "lodash";
import SourceWizard from "./SourceWizard";
import CreateModel from "./CreateModel";
interface PipelineWizardProps {
  appBaseUrl: string;
  curWizardStepGroup: string | undefined;
  curWizardStep: string;
  steps: WizardSteps;
  stepGroups?: WizardSteps;
  demo: boolean;
  onFinish: (id: number) => void;
}

export interface PipelineWizardStepProps {
  appBaseUrl: string;
  curWizardStep: string;
  steps: WizardSteps;
  stepGroups?: WizardSteps;
  setCurWizardStep: (stepGroup: string | undefined, step: string) => void;
  onFinish?: (id: number) => void;
}

const ModelWizard = ({
  appBaseUrl,
  curWizardStepGroup,
  curWizardStep,
  onFinish,
  steps,
  demo,
}: PipelineWizardProps) => {
  const router = useRouter();
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  const wizardStepKey = routerUtils.getString(router.query.wizardStep);
  const setCurWizardStep = (stepGroup: string | undefined, step: string) => {
    wizardUtils.setCurWizardStep(router, stepGroup, step);
  };

  useEffect(() => {
    if (!pipelineWizContext) return;

    if (wizardStepKey === "source:selectType") {
      setPipelineWizContext({});
    }
  }, [wizardStepKey, !!pipelineWizContext, demo]);
  if (!curWizardStepGroup && !curWizardStep) {
    if (demo) {
      setCurWizardStep("source", "model");
    } else {
      setCurWizardStep("source", "selectType");
    }
    return <Loading />;
  }
  return (
    <>
      {curWizardStepGroup === "source" && (
        <SourceWizard
          appBaseUrl={appBaseUrl}
          curWizardStep={curWizardStep}
          stepGroups={steps}
          steps={{
            selectType: {
              title: "Select Source Type",
              description: "Which warehouse do you own?",
            },
            configure: {
              title: "Configure Warehouse",
              description:
                "Follow the guide on the right to set up your Source or invite a team member to do it for you",
            },
            selectExisting: {
              title: "Select Warehouse",
              description:
                "To get the data you need, we need to select warehouse. If you don't have a warehouse, you can set a Castled-management warehouse or invite a team member who has access to warehouse.",
            },
            modelType: {
              title: "Select Model Type",
              description: "Select the type of model you want to create.",
            },
          }}
          setCurWizardStep={setCurWizardStep}
          onFinish={onFinish}
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

export default ModelWizard;
