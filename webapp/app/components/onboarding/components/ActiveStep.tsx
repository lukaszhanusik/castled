import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "../WelcomeOnboarding";
import AccordionBody from "./AccordionBody";

interface ActiveStepProps {
  steps: WelcomeOnboardingData[];
  activeStep: number | undefined | null;
}

export default function ActiveStep({ activeStep, steps }: ActiveStepProps) {
  return (
    <>
      {activeStep === 0 && activeStep !== undefined && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {/* Below component is for when nothing is configured. Need to explicitly 
      check undefined Otherwise it will render 0 string.*/}
      {activeStep !== undefined && activeStep !== null && activeStep > 0 && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {/* Below component is for when atleast one thing is configured. Need to explicitly 
      check undefined and greater than one done, Otherwise it will render 0 string.*/}
      {activeStep === null && (
        <Accordion defaultActiveKey="null">
          <AccordionBody steps={steps} />
        </Accordion>
      )}
    </>
  );
}
