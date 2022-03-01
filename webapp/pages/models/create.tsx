import React from "react";
import { GetServerSidePropsContext } from "next";
import routerUtils from "@/app/common/utils/routerUtils";
import PipelineWizardProvider, {
  usePipelineWizContext,
} from "@/app/common/context/pipelineWizardContext";
import { useRouter } from "next/router";
import wizardUtils from "@/app/common/utils/wizardUtils";
import ModelWizard from "../../app/components/model/ModelWizard";

export async function getServerSideProps({ query }: GetServerSidePropsContext) {
  const wizardStep = routerUtils.getString(query.wizardStep);
  const demo = routerUtils.getBoolean(query.demo);
  return {
    props: {
      wizardStepKey: wizardStep,
      appBaseUrl: process.env.APP_BASE_URL,
      demo,
    },
  };
}

interface ModelCreateProps {
  wizardStepKey: string;
  appBaseUrl: string;
  demo: boolean;
}

const ModelCreate = ({ wizardStepKey, appBaseUrl, demo }: ModelCreateProps) => {
  const router = useRouter();
  const { setPipelineWizContext } = usePipelineWizContext();
  const [wizardStepGroup, wizardStep] =
    wizardUtils.getWizardStepAndGroup(wizardStepKey);
  return (
    <PipelineWizardProvider>
      <ModelWizard
        appBaseUrl={appBaseUrl}
        curWizardStepGroup={wizardStepGroup}
        curWizardStep={wizardStep}
        demo={demo}
        steps={{
          source: {
            title: "Confgure Source",
            description: "",
            stepKey: "selectType",
          },
          configure: {
            title: "Create Model",
            description: "Create Model",
            stepKey: "configureModel",
          },
        }}
        onFinish={() => {
          if (process.browser) {
            router.push(`/models`).then(() => {
              setPipelineWizContext({});
            });
          }
        }}
      />
    </PipelineWizardProvider>
  );
};

export default ModelCreate;
