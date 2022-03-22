import { useEffect } from "react";
import ConnectorView from "@/app/components/connectors/ConnectorView";
import { onboardingContext } from "@/app/components/onboarding/utils/onboardingContext";

const Apps = () => {
  useEffect(() => {
    onboardingContext("default");
  }, []);
  return <ConnectorView category="App" />;
};

export default Apps;
