import {
  IconApps,
  IconGitCompare,
  IconDatabase,
  IconRelationManyToMany,
} from "@tabler/icons";

const demoOnboardingSteps = [
  {
    title: "Configure Source",
    description:
      "Since you do not have a warehouse configured, use the pre-configured warehouse provided by Castled.",
    type: "warehouses",
    icon: IconDatabase,
    isDone: true,
    buttonText: "",
    onClickURL: "/pipelines/create?demo=1",
  },
  {
    title: "Create Model",
    description:
      "Castled provides a demo model which will be used to query the demo warehouse table.",
    type: "models",
    icon: IconRelationManyToMany,
    isDone: true,
    buttonText: "",
    onClickURL: "/pipelines/create?demo=1",
  },
  {
    title: "Configure Destination",
    description:
      "Configure your Destination App. Castled enables you to move data to all major sales, marketing, service apps.",
    type: "apps",
    icon: IconApps,
    isDone: false,
    buttonText: "Add App",
    onClickURL: "/apps/create?wizardStep=selectType",
  },
  {
    title: "Create Pipeline",
    description:
      "Create your first pipeline to enable your business teams, by moving the data to the app with which they work.",
    type: "pipelines",
    icon: IconGitCompare,
    isDone: false,
    buttonText: "Create Pipeline",
    onClickURL: "/pipelines/create?demo=1",
  },
];

const onboardingSteps = [
  {
    title: "Configure Source",
    description:
      "Start with configuring your Source. Castled supports all major warehouses including Snowflake, Redshift, Postgres and Bigquery.",
    type: "warehouses",
    icon: IconDatabase,
    isDone: false,
    buttonText: "Add Warehouse",
    onClickURL: "/warehouses/create?wizardStep=selectType",
  },
  {
    title: "Create Model",
    description:
      "Once you have configured the source warehouse, create a model out of the data in your warehouse tables.",
    type: "models",
    icon: IconRelationManyToMany,
    isDone: false,
    buttonText: "Create Model",
    onClickURL: "/models/create?wizardStep=source:selectType",
  },
  {
    title: "Configure Destination",
    description:
      "Configure your Destination App. Castled enables you to move data to all major sales, marketing, service apps.",
    type: "apps",
    icon: IconApps,
    isDone: false,
    buttonText: "Add App",
    onClickURL: "/apps/create?wizardStep=selectType",
  },
  {
    title: "Create Pipeline",
    description:
      "Create your first pipeline to enable your business teams, by moving the data to the app with which they work.",
    type: "pipelines",
    icon: IconGitCompare,
    isDone: false,
    buttonText: "Create Pipeline",
    onClickURL: "/pipelines/create?wizardStep=source:selectModelType",
  },
];

export { onboardingSteps, demoOnboardingSteps };
