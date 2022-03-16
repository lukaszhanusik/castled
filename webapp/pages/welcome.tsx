import { useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { useSession } from "@/app/common/context/sessionContext";
import WelcomePopup from "@/app/components/layout/WelcomePopup";
import WelcomeOnboarding from "@/app/components/onboarding/Welcome";
import PrimaryButtons from "@/app/components/onboarding/PrimaryButtons";

const Welcome = () => {
  const [showSteps, setShowSteps] = useState(false);
  const [btnType, setBtnType] = useState("default");
  const { isOss } = useSession();

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
            className="btn btn-primary"
            onClick={() => stepsToggle(false, "default")}
          >
            {"<-"} Go back
          </button>
        )}
        {!isOss && (btnType === "demo" || btnType === "default") && (
          <PrimaryButtons
            stepsToggle={() => stepsToggle(true, "demo")}
            btnType="demo"
          />
        )}
        {/* {typeof window === "object" && isOss && <WelcomePopup />} */}
        {(btnType === "primary" || btnType === "default") && (
          <PrimaryButtons
            stepsToggle={() => stepsToggle(true, "primary")}
            btnType="primary"
          />
        )}
        {showSteps && (
          <div className="mb-5 px-3">
            <WelcomeOnboarding type={btnType} />
          </div>
        )}
      </div>
    </Layout>
  );
};

export default Welcome;
