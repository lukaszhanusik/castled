import pipelineService from "@/app/services/pipelineService";
import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Container, Navbar } from "react-bootstrap";
import { onboardingSteps } from "./data/onboardingSteps";
import { WelcomeOnboardingData } from "./Welcome";

export default function OnboardingBanner() {
  const [steps, setSteps] = useState<WelcomeOnboardingData[]>([]);
  const [countDone, setCountDone] = useState(0);
  const router = useRouter();

  useEffect(() => {
    pipelineService
      .onboardCount()
      .then(({ data }) => {
        const updatedOnboarding = onboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        setSteps(updatedOnboarding);
        setCountDone(Object.values(data).filter(Boolean).length);
      })
      .catch(() => {
        console.log("error");
      });
  }, []);

  return (
    <>
      {!router.pathname.includes("welcome") && countDone < 4 && (
        <>
          <Navbar
            bg="secondary"
            variant="light"
            style={{ backgroundColor: "#dff6f8c9" }}
          >
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
                          step.isDone
                            ? "onboarding done"
                            : "onboarding not-done"
                        }`}
                        width="30"
                        height="30"
                        style={{ width: "1.2rem" }}
                      />
                    ) : (
                      <Icon size={25} stroke={1} className="navbar-icon" />
                    )}{" "}
                    <label style={{ fontWeight: 500 }}>{step.title}</label>
                  </Navbar.Brand>
                );
              })}
            </Container>
          </Navbar>
        </>
      )}
    </>
  );
}
