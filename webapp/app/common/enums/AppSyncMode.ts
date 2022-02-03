export enum AppSyncMode {
  INSERT = "INSERT",
  UPSERT = "UPSERT",
  UPDATE = "UPDATE",
}

export const AppSyncModeLabel: { [key in AppSyncMode]: string } = {
  [AppSyncMode.INSERT]: "Insert",
  [AppSyncMode.UPSERT]: "Upsert",
  [AppSyncMode.UPDATE]: "Update",
};
