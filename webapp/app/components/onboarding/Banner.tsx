import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Container, Navbar } from "react-bootstrap";

function OnboardingBanner() {
  const [countDone, setCountDone] = useState(0);
  const [pathName, setPathName] = useState("");
  const router = useRouter();

  useEffect(() => {
    const getCount = localStorage.getItem("onboarding_count");
    if (getCount) {
      setCountDone(JSON.parse(getCount));
    }
    setPathName(router.pathname);
  }, []);

  return (
    <>
      {!pathName.includes("welcome") && countDone < 4 && (
        <>
          <Navbar style={{ backgroundColor: "#7a73ff" }}>
            <Container className="justify-content-around">
              <Navbar.Brand className="banner-primary">
                <label className="banner-text">
                  Complete the pending onboarding steps to start moving your
                  data.
                </label>
                <button
                  className="banner-btn"
                  onClick={() => router.push("/welcome?redirect=banner")}
                >
                  {countDone === 0 ? "Start Onboarding" : "Continue Onboarding"}
                </button>
              </Navbar.Brand>
              {/* {steps.map((step, index) => {
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
              })} */}
            </Container>
          </Navbar>
        </>
      )}
    </>
  );
}

export default OnboardingBanner;
