import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "./Welcome";

export default function ConditionalStep({
  steps,
}: {
  steps: WelcomeOnboardingData[];
}) {
  const router = useRouter();
  const [activeStep, setActiveStep] = useState<string | undefined>();
  const [countDone, setCountDone] = useState(0);

  useEffect(() => {
    const step = findRemainingStep();
    if (step) {
      setActiveStep(step.toString());
    }
    const countIsDone = steps.reduce(
      (acc, curr) => (curr.isDone ? acc + 1 : 0),
      0
    );
    setCountDone(countIsDone);
  }, [steps]);

  function findRemainingStep() {
    let index = 0;
    for (const step of steps) {
      if (!step.isDone) {
        return index;
      }
      index++;
    }
  }

  /* countDone is used here as a workaround for accordion defaultActiveKey which only 
   works during first render. Usually first render will give us undefined. So we will create
   a countDone state. initially it will also give undefined. Now since countDone is undefined, 
   accordion won't render and will only render when both countDone and activeStep are defined, 
   which gives us the correct render and then auto-opens the desired dropdown. */
  return (
    <>
      {countDone && (
        <Accordion defaultActiveKey={activeStep}>
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
                          step.isDone
                            ? "onboarding done"
                            : "onboarding not-done"
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
        </Accordion>
      )}
    </>
  );
}
