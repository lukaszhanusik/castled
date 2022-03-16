import { useEffect, useState } from "react";
import { Alert } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import { useRouter } from "next/router";
import { onboardingSteps, demoOnboardingSteps } from "./data/onboardingSteps";
import { TablerIcon } from "@tabler/icons";
import conditionalStep from "./conditionalStep";

export interface WelcomeOnboardingData {
  title: string;
  description: string;
  isDone: boolean;
  type: string;
  icon: TablerIcon;
  onClickURL: string;
}

export default function WelcomeOnboarding({ type }: { type: string }) {
  const [steps, setSteps] = useState<WelcomeOnboardingData[]>([]);

  useEffect(() => {
    pipelineService
      .onboardCount()
      .then(({ data }) => {
        const updatedOnboarding = onboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        setSteps(updatedOnboarding);
      })
      .catch(() => {
        console.log("error");
      });
  }, []);

  return <>{conditionalStep(type === "demo" ? demoOnboardingSteps : steps)}</>;
}
