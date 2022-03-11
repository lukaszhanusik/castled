import pipelineService from "@/app/services/pipelineService";
import { useState, useEffect } from "react";
import { Container, Navbar } from "react-bootstrap";
import onboardingSteps from "./data/onboardingSteps";
import { WelcomeOnboardingProps } from "./Welcome";

export default function OnboardingBanner() {
  const [steps, setSteps] = useState<WelcomeOnboardingProps[]>([]);

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
      <Navbar bg="secondary" variant="light">
        {
          <Container className="justify-content-around">
            {steps.map((step, index) => {
              const Icon = step.icon;
              return (
                <Navbar.Brand
                  href={!step.isDone ? step.onClickURL : "#"}
                  key={step.type}
                >
                  {step.isDone ? (
                    <img
                      src="/images/check-filled.svg"
                      alt="Completed or not"
                      className={`d-inline-block align-top ${
                        step.isDone ? "onboarding done" : "onboarding not-done"
                      }`}
                      width="30"
                      height="30"
                      style={{ width: "1.2rem" }}
                    />
                  ) : (
                    <Icon size={25} stroke={1} className="navbar-icon" />
                  )}{" "}
                  <label className="">{step.title}</label>
                </Navbar.Brand>
              );
            })}
          </Container>
        }
      </Navbar>
    </>
  );
}
