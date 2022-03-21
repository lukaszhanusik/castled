import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "../WelcomeOnboarding";

export default function ConditionalStep({
  steps,
  type,
}: {
  steps: WelcomeOnboardingData[];
  type: string;
}) {
  const [activeStep, setActiveStep] = useState<number | undefined>();

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
    return 0;
  }

  return (
    <>
      {type === "demo" && !!activeStep && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {/* Below component is for when nothing is configured. Need to explicitly 
      check undefined Otherwise it will render 0 string.*/}
      {type === "primary" && activeStep === 0 && activeStep !== undefined && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
      {/* Below component is for when atleast one thing is configured. Need to explicitly 
      check undefined and greater than one done, Otherwise it will render 0 string.*/}
      {type === "primary" && activeStep !== undefined && activeStep > 0 && (
        <Accordion defaultActiveKey={`${activeStep}`}>
          <AccordionBody steps={steps} />
        </Accordion>
      )}
    </>
  );
}

function AccordionBody({ steps }: { steps: WelcomeOnboardingData[] }) {
  const router = useRouter();

  return (
    <>
      {steps.map((step, index) => {
        const Icon = step.icon;
        return (
          <Accordion.Item eventKey={`${index}`} key={index}>
            <Accordion.Header>
              <div className="mx-2">
                {step.isDone ? (
                  <img
                    src="/images/check-filled.svg"
                    alt="Completed or not"
                    className={`p-0 border-0 ${
                      step.isDone ? "onboarding done" : "onboarding not-done"
                    }`}
                    style={{ width: "1.2rem" }}
                  />
                ) : (
                  <Icon size={25} stroke={1} className="navbar-icon" />
                )}
              </div>
              <div className="mx-2">{step.title}</div>
            </Accordion.Header>
            <Accordion.Body>
              <div className="my-2">{step.description}</div>
              {step.buttonText && (
                <div className="my-3">
                  <button
                    className="btn btn-primary"
                    onClick={() => router.push(step.onClickURL)}
                  >
                    {"+ " + step.buttonText}
                  </button>
                </div>
              )}
            </Accordion.Body>
          </Accordion.Item>
        );
      })}
    </>
  );
}
