export enum ConnectorCategory {
  WAREHOUSE = "Warehouse",
  APP = "App",
  MODEL = "Model",
}

export const ConnectorCategoryLabel: { [key in ConnectorCategory]: string } = {
  [ConnectorCategory.WAREHOUSE]: "Source",
  [ConnectorCategory.APP]: "Destination",
  [ConnectorCategory.MODEL]: "Model",
};
