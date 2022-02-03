export enum PipelineStatus {
  OK = "OK",
  FAILED = "FAILED",
}

export const PipelineStatusLabel: { [key in PipelineStatus]: string } = {
  [PipelineStatus.OK]: "OK",
  [PipelineStatus.FAILED]: "Failed",
};
