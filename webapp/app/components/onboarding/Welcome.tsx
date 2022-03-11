import { useEffect, useState } from "react";
import { Alert } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import { useRouter } from "next/router";
import onboardingSteps from "./data/onboardingSteps";
import { TablerIcon } from "@tabler/icons";

export interface WelcomeOnboardingProps {
  title: string;
  description: string;
  isDone: boolean;
  type: string;
  icon: TablerIcon;
  onClickURL: string;
}

export default function WelcomeOnboarding() {
  const [steps, setSteps] = useState<WelcomeOnboardingProps[]>([]);
  const router = useRouter();

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

  return (
    <>
      {steps.map((step, index) => {
        const Icon = step.icon;
        return (
          <div key={step.type}>
            {!step.isDone && (
              <Alert
                variant={step.isDone ? "primary" : "light"}
                className="d-flex flex-row card border"
                onClick={() => !step.isDone && router.push(step.onClickURL)}
                style={{
                  cursor: !step.isDone ? "pointer" : "default",
                  columnGap: "1rem",
                }}
              >
                {step.isDone ? (
                  <img
                    src={"/images/check-filled.svg"}
                    alt="Completed or not"
                    className={`p-0 border-0 ${
                      step.isDone ? "onboarding done" : "onboarding not-done"
                    }`}
                    style={{ width: "1.5rem" }}
                  />
                ) : (
                  <Icon size={30} stroke={1} className="navbar-icon" />
                )}
                <div className="">
                  <Alert.Heading>{step.title}</Alert.Heading>
                  <div>{step.description}</div>
                </div>
              </Alert>
            )}
          </div>
        );
      })}
    </>
  );
}
