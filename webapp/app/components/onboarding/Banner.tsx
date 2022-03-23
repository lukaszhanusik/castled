import { useRouter } from "next/router";
import { useState, useEffect } from "react";
import { Button, Container, Navbar } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";

function OnboardingBanner() {
  const [demoCountDone, setDemoCountDone] = useState(0);
  const [ownCountDone, setOwnCountDone] = useState(0);
  const [countDone, setCountDone] = useState(0);

  const [pathName, setPathName] = useState("");
  const router = useRouter();

  useEffect(() => {
    setPathName(router.pathname);
    const getOwnCount = localStorage.getItem("onboarding_count");
    const getDemoCount = localStorage.getItem("demo_onboarding_count");

    if (getOwnCount && getDemoCount) {
      setOwnCountDone(JSON.parse(getOwnCount));
      setDemoCountDone(JSON.parse(getDemoCount));
    } else {
      pipelineService
        .onboardCount()
        .then(({ data }) => {
          for (let [key, value] of Object.entries(data)) {
            if (key === "pipelines") {
              setOwnCountDone(ownCountDone + value);
            }
            if (key === "demoPipeline") {
              setDemoCountDone(demoCountDone + value);
            }
          }

          localStorage.setItem(
            "onboarding_count",
            JSON.stringify(data.pipelines)
          );
          localStorage.setItem(
            "demo_onboarding_count",
            JSON.stringify(data.demoPipelines)
          );
        })
        .catch(() => {
          console.log("Error fetching pipeline count.");
        });
    }
  }, []);

  useEffect(() => {
    const getAllStepCount = localStorage.getItem("all_count_done");
    if (getAllStepCount) {
      setCountDone(JSON.parse(getAllStepCount));
    } else {
      pipelineService.onboardCount().then(({ data }) => {
        const onboardCount = Object.values(data).reduce(
          (acc: number, curr: number) => {
            if (curr) {
              return acc + 1;
            }
            return acc;
          }
        );

        localStorage.setItem("all_count_done", JSON.stringify(onboardCount));
        setCountDone(onboardCount);
      });
    }
  }, []);
  return (
    <>
      {pathName.includes("welcome")
        ? ""
        : !ownCountDone && (
            <>
              <Navbar style={{ backgroundColor: "#5c2e8a" }}>
                <Container className="justify-content-around">
                  <Navbar.Brand className="banner-primary align-items-center">
                    <label className="banner-text">
                      {!demoCountDone
                        ? "Complete the pending onboarding steps to start moving your data."
                        : !ownCountDone &&
                          "Complete the pending onboarding steps to create your first pipeline using your own warehouse."}
                    </label>
                    <Button
                      size="sm"
                      variant="warning"
                      className="btn banner-btn"
                      onClick={() => router.push("/welcome")}
                    >
                      {!countDone
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
