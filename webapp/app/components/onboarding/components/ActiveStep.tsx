import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "../WelcomeOnboarding";
import AccordionBody from "./AccordionBody";

interface ActiveStepProps {
  steps: WelcomeOnboardingData[];
  activeStep: number | undefined | null;
}

export default function ActiveStep({
  activeStep,
  steps,
}: ActiveStepProps) {
  return (
    <>
      {activeStep === 0 && activeStep !== undefined && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {activeStep !== undefined && activeStep !== null && activeStep > 0 && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {activeStep === null && (
        <Accordion defaultActiveKey="null">
          <AccordionBody steps={steps} />
        </Accordion>
      )}
    </>
  );
}
