import { useEffect, useState } from "react";
import { Alert } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import { useRouter } from "next/router";

const onboardingSteps = [
  {
    title: "Configure Source",
    description:
      "Start by connecting Castled to a source like Snowflake, Postgres, BigQuery, and many more.",
    type: "warehouses",
    href: "/welcome/source",
    isDone: false,
    onClickURL: "/warehouses/create?wizardStep=selectType",
  },
  {
    title: "Create Model",
    description:
      "Use a model to build a specific view of the data in your source.",
    type: "models",
    href: "/welcome/model",
    isDone: false,
    onClickURL: "/models/create?wizardStep=source:selectType",
  },
  {
    title: "Configure Destination",
    description:
      "Connect Castled to your favorite tools like Salesforce, HubSpot, Facebook Ads, and many more. ",
    type: "apps",
    href: "/welcome/apps",
    isDone: false,
    onClickURL: "/apps/create?wizardStep=selectType",
  },
  {
    title: "Create Pipeline",
    description:
      "Operationalize your data by syncing a model to a destination.",
    type: "pipelines",
    href: "/welcome/sync",
    isDone: false,
    onClickURL: "/pipelines/create?wizardStep=source:selectModelType",
  },
];

interface WelcomeOnboardingProps {
  title: string;
  description: string;
  isDone: boolean;
  type: string;
  href: string;
  onClickURL: string;
}

export default function WelcomeOnboarding() {
  const [steps, setSteps] = useState<WelcomeOnboardingProps[]>([]);
  const router = useRouter();

  useEffect(() => {
    pipelineService
      .onboardCount()
      .then(({ data }) => {
        const updatedOnboarding = onboardingSteps.map((step) =>
          data[step.type] ? { ...step, isDone: true } : step
        );
        setSteps(updatedOnboarding);
      })
      .catch(() => {
        console.log("error");
      });
  }, []);

  return (
    <>
      {steps.map((step, index) => (
        <div key={step.type}>
          <Alert
            variant={step.isDone ? "success" : "light"}
            className="pb-0 card border"
            onClick={() => !step.isDone && router.push(step.onClickURL)}
            style={{ cursor: !step.isDone ? "pointer" : "default" }}
          >
            <Alert.Heading>{step.title}</Alert.Heading>
            <p>{step.description}</p>
          </Alert>
        </div>
      ))}
    </>
  );
}
