import { useRouter } from "next/router";
import { Accordion } from "react-bootstrap";
import { WelcomeOnboardingData } from "./Welcome";

export default function ConditionalStep({
  steps,
}: {
  steps: WelcomeOnboardingData[];
}) {
  const router = useRouter();
  return (
    <Accordion>
      {steps.map((step, index) => {
        const Icon = step.icon;
        return (
          <Accordion.Item eventKey={`${index}`}>
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
    </Accordion>
  );
}
