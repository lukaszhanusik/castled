export enum ConnectorCategory {
  WAREHOUSE = "Warehouse",
  APP = "App",
}

export const ConnectorCategoryLabel: { [key in ConnectorCategory]: string } = {
  [ConnectorCategory.WAREHOUSE]: "Source",
  [ConnectorCategory.APP]: "Destination",
};
