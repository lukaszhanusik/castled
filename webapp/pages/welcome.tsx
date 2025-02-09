import { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { useSession } from "@/app/common/context/sessionContext";
import WelcomeOnboarding from "@/app/components/onboarding/WelcomeOnboarding";
import PrimaryButtons from "@/app/components/onboarding/components/PrimaryButtons";
import {
  demoOnboardingSteps,
  onboardingSteps,
} from "@/app/components/onboarding/data/onboardingSteps";
import pipelineService from "@/app/services/pipelineService";
import { IconChevronLeft } from "@tabler/icons";
import {
  onboardingContext,
  setStepsDoneCount,
} from "@/app/components/onboarding/utils/onboardingContext";

const Welcome = () => {
  const [showSteps, setShowSteps] = useState(false);
  const [btnType, setBtnType] = useState("default");
  const [demoCompletedCount, setDemoCompletedCount] = useState<number | undefined>();
  const [primaryCompletedCount, setPrimaryCompletedCount] = useState<number | undefined>();
  const { isOss } = useSession();

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

        setStepsDoneCount(data);
      })
      .catch(() => {
        console.log("Error fetching pipeline count.");
      });
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
    onboardingContext(type);
  }

  return (
    <Layout title="Welcome" subTitle={undefined} hideHeader>
      <div className="welcome-wrapper">
        <p className="mb-0">Welcome to Castled!</p>
        <h2 className="mb-4">Get started with your first pipeline.</h2>
        {(btnType === "primary" || btnType === "demo") && (
          <button
            className="btn btn-outline-primary mb-3"
            onClick={() => stepsToggle(false, "default")}
          >
            <IconChevronLeft size={16} /> Go back
          </button>
        )}
        <div className={showSteps ? "bg" : ""}>
          {!isOss && (btnType === "demo" || btnType === "default") && (
            <PrimaryButtons
              stepsToggle={() => stepsToggle(true, "demo")}
              btnType="demo"
              doneCount={demoCompletedCount}
              showSteps={showSteps}
            />
          )}
          {(btnType === "primary" || btnType === "default") && (
            <PrimaryButtons
              stepsToggle={() => stepsToggle(true, "primary")}
              btnType="primary"
              doneCount={primaryCompletedCount}
              showSteps={showSteps}
            />
          )}
          {showSteps && (
            <div className="mb-3">
              <WelcomeOnboarding type={btnType} />
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
};

export default Welcome;
