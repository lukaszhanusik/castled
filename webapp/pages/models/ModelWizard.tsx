import React, { useEffect } from "react";
import { WizardSteps } from "@/app/common/dtos/internal/WizardSteps";
import { useRouter } from "next/router";
import wizardUtils from "@/app/common/utils/wizardUtils";
import Loading from "@/app/components/common/Loading";
import PipelineWizardDestination from "@/app/components/pipeline/step2destination/PipelineWizardDestination";
import PipelineMapping from "@/app/components/pipeline/step3mapping/PipelineMapping";
import PipelineSettings from "@/app/components/pipeline/step4settings/PipelineSettings";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import routerUtils from "@/app/common/utils/routerUtils";
import _ from "lodash";
import { AccessType } from "@/app/common/enums/AccessType";
import { PipelineWizardContextDto } from "@/app/common/dtos/context/PipelineWizardContextDto";
import warehouseService from "@/app/services/warehouseService";
import SourceWizard from "./SourceWizard";

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
        />
      )}
      {curWizardStepGroup === undefined && curWizardStep === "configure" && (
        <div>configure</div>
        // <PipelineMapping
        //   appBaseUrl={appBaseUrl}
        //   curWizardStep={curWizardStep}
        //   stepGroups={steps}
        //   steps={{
        //     mapping: {
        //       title: "Map fields",
        //       description:
        //         "Map source columns to the fields in the destination. Select primary keys based on which deduplication should happen",
        //     },
        //   }}
        //   setCurWizardStep={setCurWizardStep}
        // />
      )}
      {curWizardStepGroup === undefined && curWizardStep === "settings" && (
        <div>settings</div>
        // <PipelineSettings
        //   appBaseUrl={appBaseUrl}
        //   curWizardStep={curWizardStep}
        //   stepGroups={steps}
        //   steps={{
        //     settings: {
        //       title: "Final Settings",
        //       description:
        //         "Almost there, give a name to your model and save for use in pipeline setup.",
        //     },
        //   }}
        //   setCurWizardStep={setCurWizardStep}
        //   onFinish={onFinish}
        // />
      )}
    </>
  );
};

export default ModelWizard;
