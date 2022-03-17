import { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { useSession } from "@/app/common/context/sessionContext";
import WelcomePopup from "@/app/components/layout/WelcomePopup";
import WelcomeOnboarding from "@/app/components/onboarding/Welcome";
import PrimaryButtons from "@/app/components/onboarding/PrimaryButtons";
import {
  demoOnboardingSteps,
  onboardingSteps,
} from "@/app/components/onboarding/data/onboardingSteps";
import pipelineService from "@/app/services/pipelineService";
import { IconChevronLeft } from "@tabler/icons";
import { useRouter } from "next/router";

const Welcome = () => {
  const [showSteps, setShowSteps] = useState(false);
  const [btnType, setBtnType] = useState("default");
  const [demoCompletedCount, setDemoCompletedCount] = useState(0);
  const [primaryCompletedCount, setPrimaryCompletedCount] = useState(0);
  const { isOss } = useSession();
  const router = useRouter();

  useEffect(() => {
    pipelineService
      .onboardCount()
      .then(({ data }) => {
        const updatedDemoOnboarding = demoOnboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        const updatedPrimaryOnboarding = onboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        setDemoCompletedCount(isDoneCounter(updatedDemoOnboarding));
        setPrimaryCompletedCount(isDoneCounter(updatedPrimaryOnboarding));
        localStorage.setItem(
          "onboarding_count",
          JSON.stringify(isDoneCounter(updatedPrimaryOnboarding))
        );
      })
      .catch(() => {
        console.log("Error fetching pipeline count.");
      });

    if (router.query.redirect === "banner") {
      setBtnType("primary");
      setShowSteps(true);
    }
  }, []);

  function isDoneCounter(item: any) {
    return item.reduce((acc: number, curr: { isDone: boolean }) => {
      if (curr.isDone) {
        return acc + 1;
      }
      return acc;
    }, 0);
  }

  function stepsToggle(show: boolean, type: string) {
    setShowSteps(show);
    setBtnType(type);
  }

  return (
    <Layout title="Welcome" subTitle={undefined} hideHeader>
      <div className="welcome-wrapper">
        <p className="mb-0">Welcome to Castled!</p>
        <h2 className="mb-4">Get started with your first pipeline.</h2>
        {(btnType === "primary" || btnType === "demo") && (
          <button
            className="btn btn-primary mb-3"
            onClick={() => stepsToggle(false, "default")}
          >
            <IconChevronLeft size={20} /> Go back
          </button>
        )}
        <div className={showSteps ? "border border-2" : ""}>
          {!isOss && (btnType === "demo" || btnType === "default") && (
            <PrimaryButtons
              stepsToggle={() => stepsToggle(true, "demo")}
              btnType="demo"
              doneCount={demoCompletedCount}
              showSteps={showSteps}
            />
          )}
          {/* {typeof window === "object" && isOss && <WelcomePopup />} */}
          {(btnType === "primary" || btnType === "default") && (
            <PrimaryButtons
              stepsToggle={() => stepsToggle(true, "primary")}
              btnType="primary"
              doneCount={primaryCompletedCount}
              showSteps={showSteps}
            />
          )}
          {showSteps && (
            <div className="mb-3 px-3">
              <WelcomeOnboarding type={btnType} />
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default Welcome;
