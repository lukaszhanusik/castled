import { useEffect, useState } from "react";
import pipelineService from "@/app/services/pipelineService";
import { onboardingSteps, demoOnboardingSteps } from "./data/onboardingSteps";
import { TablerIcon } from "@tabler/icons";
import ConditionalStep from "@/app/components/onboarding/conditionalStep";

export interface WelcomeOnboardingData {
  title: string;
  description: string;
  isDone: boolean;
  type: string;
  icon: TablerIcon;
  buttonText: string;
  onClickURL: string;
}

export default function WelcomeOnboarding({ type }: { type: string }) {
  const [demoSteps, setDemoSteps] = useState<WelcomeOnboardingData[]>([]);
  const [steps, setSteps] = useState<WelcomeOnboardingData[]>([]);

  useEffect(() => {
    pipelineService
      .onboardCount()
      .then(({ data }) => {
        const updatedDemoOnboarding = demoOnboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        const updatedOnboarding = onboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        setDemoSteps(updatedDemoOnboarding);
        setSteps(updatedOnboarding);
      })
      .catch(() => {
        console.log("error");
      });
  }, []);

  return (
    <>
      <ConditionalStep steps={type === "demo" ? demoSteps : steps} type={type}/>
    </>
  );
}
