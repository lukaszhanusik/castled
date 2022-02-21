export enum PipelineSyncStatus {
  ACTIVE = "ACTIVE",
  PAUSED = "PAUSED",
}

export const PipelineSyncStatusLabel: { [key in PipelineSyncStatus]: string } = {
  [PipelineSyncStatus.ACTIVE]: "Active",
  [PipelineSyncStatus.PAUSED]: "Paused",
};
