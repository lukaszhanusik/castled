import { useEffect } from "react";
import ConnectorView from "@/app/components/connectors/ConnectorView";
import { onboardingContext } from "@/app/components/onboarding/utils/OnboardingContext";

const Warehouses = () => {
  useEffect(() => {
    onboardingContext("default");
  }, []);
  return <ConnectorView category="Warehouse" />;
};

export default Warehouses;
