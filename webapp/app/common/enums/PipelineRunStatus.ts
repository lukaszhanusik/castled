export enum PipelineRunStatus {
  PROCESSING = "PROCESSING",
  PROCESSED = "PROCESSED",
  FAILED = "FAILED",
}

export const PipelineRunStatusLabel: { [key in PipelineRunStatus]: string } = {
  [PipelineRunStatus.PROCESSING]: "Processing",
  [PipelineRunStatus.PROCESSED]: "Completed",
  [PipelineRunStatus.FAILED]: "Failed",
};
