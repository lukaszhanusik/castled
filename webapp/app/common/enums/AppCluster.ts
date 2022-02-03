export enum AppCluster {
  INDIA = "INDIA",
  US = "US",
}

export const AppClusterLabel: { [key in AppCluster]: string } = {
  [AppCluster.INDIA]: "India",
  [AppCluster.US]: "US",
};
