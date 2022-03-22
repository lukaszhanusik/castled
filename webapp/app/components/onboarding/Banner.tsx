import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Button, Container, Navbar } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";

function OnboardingBanner() {
  const [countDone, setCountDone] = useState(0);
  const [pathName, setPathName] = useState("");
  const router = useRouter();

  useEffect(() => {
    setPathName(router.pathname);
    const getCount = localStorage.getItem("onboarding_count");
    if (getCount) {
      setCountDone(JSON.parse(getCount));
    } else {
      pipelineService
        .onboardCount()
        .then(({ data }) => {
          const onboardCount = Object.values(data).reduce(
            (acc: number, curr: number) => {
              if (curr) {
                return acc + 1;
              }
              return acc;
            }
          );
          setCountDone(onboardCount);
          localStorage.setItem(
            "onboarding_count",
            JSON.stringify(onboardCount)
          );
        })
        .catch(() => {
          console.log("Error fetching pipeline count.");
        });
    }
  }, []);

  return (
    <>
      {pathName.includes("welcome")
        ? ""
        : countDone < 4 && (
            <>
              <Navbar style={{ backgroundColor: "#5c2e8a" }}>
                <Container className="justify-content-around">
                  <Navbar.Brand className="banner-primary align-items-center">
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
                </Container>
              </Navbar>
            </>
          )}
    </>
  );
}

export default OnboardingBanner;
