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
      "Start with configuring your Source. Castled supports all major warehouses including Snowflake, Redshift, Postgres and Bigquery.",
    type: "warehouses",
    icon: IconDatabase,
    isDone: true,
    onClickURL: "/warehouses/create?wizardStep=selectType",
  },
  {
    title: "Create Model",
    description:
      "Once you have configured the source warehouse, create a model out of the data in your warehouse tables.",
    type: "models",
    icon: IconRelationManyToMany,
    isDone: true,
    onClickURL: "/models/create?wizardStep=source:selectType",
  },
  {
    title: "Configure Destination",
    description:
      "Configure your Destination App. Castled enables you to move data to all major sales, marketing, service apps.",
    type: "apps",
    icon: IconApps,
    isDone: false,
    onClickURL: "/apps/create?wizardStep=selectType",
  },
  {
    title: "Create Pipeline",
    description:
      "Create your first pipeline to open the flood gates of operational analytics for your business teams.",
    type: "pipelines",
    icon: IconGitCompare,
    isDone: false,
    onClickURL: "/pipelines/create?wizardStep=source:selectModelType",
  },
];

export default onboardingSteps;
