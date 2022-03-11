import {
  IconApps,
  IconGitCompare,
  IconDatabase,
  IconRelationManyToMany,
} from "@tabler/icons";

const onboardingSteps = [
  {
    title: "Configure Source",
    description:
      "Start by connecting Castled to a source like Snowflake, Postgres, BigQuery, and many more.",
    type: "warehouses",
    icon: IconDatabase,
    isDone: false,
    onClickURL: "/warehouses/create?wizardStep=selectType",
  },
  {
    title: "Create Model",
    description:
      "Use a model to build a specific view of the data in your source.",
    type: 'models',
    icon: IconRelationManyToMany,
    isDone: false,
    onClickURL: "/models/create?wizardStep=source:selectType",
  },
  {
    title: "Configure Destination",
    description:
      "Connect Castled to your favorite tools like Salesforce, HubSpot, Facebook Ads, and many more. ",
    type: "apps",
    icon: IconApps,
    isDone: false,
    onClickURL: "/apps/create?wizardStep=selectType",
  },
  {
    title: "Create Pipeline",
    description:
      "Operationalize your data by syncing a model to a destination.",
    type: "pipelines",
    icon: IconGitCompare,
    isDone: false,
    onClickURL: "/pipelines/create?wizardStep=source:selectModelType",
  },
];

export default onboardingSteps;
