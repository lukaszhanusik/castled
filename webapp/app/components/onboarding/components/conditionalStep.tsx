import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "../WelcomeOnboarding";
import ActiveStep from "./ActiveStep";

export default function ConditionalStep({
  steps,
}: {
  steps: WelcomeOnboardingData[];
}) {
  const [activeStep, setActiveStep] = useState<number | undefined | null>();

  useEffect(() => {
    const step = findRemainingStep();
    setActiveStep(step);
  }, [steps]);

  function findRemainingStep() {
    let index = 0;
    for (const step of steps) {
      if (!step.isDone) {
        return index;
      }
      index++;
    }
    return null;
  }

  return (
    <>
      <ActiveStep steps={steps} activeStep={activeStep} />
    </>
  );
}
