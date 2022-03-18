import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Button, Container, Navbar } from "react-bootstrap";

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
          {/* <Navbar style={{ backgroundColor: "#5448A3" }}> */}
          <Navbar style={{ backgroundColor: "#5c2e8a" }}>
            <Container className="justify-content-around">
              <Navbar.Brand className="banner-primary">
                <label className="banner-text">
                  Complete the pending onboarding steps to start moving your
                  data.
                </label>
                <Button
                  size="sm"
                  variant="warning"
                  className="btn banner-btn"
                  onClick={() => router.push("/welcome?redirect=banner")}
                >
                  {countDone === 0
                    ? "Setup Pipeline"
                    : "Complete pipeline setup"}
                </Button>
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
