import React from "react";
import { Table, OverlayTrigger, Tooltip, Button } from "react-bootstrap";
import { IconFileDownload } from "@tabler/icons";
import pipelineRunsService from "@/app/services/pipelineRunsService";

import classNames from "classnames";
import TimeAgo from "react-timeago";

import {
  PipelineRunStatusLabel,
  PipelineRunStatus,
} from "@/app/common/enums/PipelineRunStatus";

import { PipelineRunDto } from "@/app/common/dtos/PipelineRunDto";

import renderUtils from "@/app/common/utils/renderUtils";

export interface PipelineRunViewProps {
  pipelineRuns: PipelineRunDto[] | null | undefined;
}

const PipelineRunView = ({ pipelineRuns }: PipelineRunViewProps) => {
  const downloadErrorReport = async (runId: number) => {
    pipelineRunsService.downloadErrorReport(runId);
  };

  return (
    <>
      {pipelineRuns && pipelineRuns.length > 0 && (
        <div className="table-responsive">
          <Table className="tr-collapse">
            <thead>
              <tr>
                <th>Run ID</th>
                <th>Synced</th>
                <th>Failed</th>
                <th>Skipped</th>
                <th>Started</th>
                <th>Time Taken</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {pipelineRuns.map((run, i) => (
                <OverlayTrigger
                  placement="right"
                  key={`pipeline-run-status-${i}`}
                  overlay={
                    run.failureMessage ? (
                      <Tooltip
                        id={`pipeline-run-status-${i}`}
                        className="tooltip"
                      >
                        {run.failureMessage ||
                          PipelineRunStatusLabel[run.status]}
                      </Tooltip>
                    ) : (
                      <></>
                    )
                  }
                >
                  <tr key={i}>
                    <td>{run.id}</td>
                    <td>{run.pipelineSyncStats.recordsSynced}</td>
                    <td>{run.pipelineSyncStats.recordsFailed}</td>
                    <td>{run.pipelineSyncStats.recordsSkipped}</td>
                    <td>
                      {run.createdTs && (
                        <TimeAgo date={run.createdTs} minPeriod={10} />
                      )}
                    </td>
                    <td>
                      {run.processedTs &&
                        renderUtils.getTimeTakenStr(
                          run.processedTs - run.createdTs
                        )}
                    </td>
                    <td>
                      <span className={"text-capitalize px-2 py-1 " +
                        (PipelineRunStatusLabel[run.status] === "Failed"
                          ? "failed"
                          : PipelineRunStatusLabel[run.status] === "Completed"
                            ? "completed"
                            : "processing pe-4")}>
                        {PipelineRunStatusLabel[run.status]}
                        {
                        (PipelineRunStatusLabel[run.status] === "Processing"
                        && <div className='float-end dot-flashing'></div>)
                        }
                      </span>
                    </td>
                    <td>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        className={classNames({
                          "px-1 d-none":
                            run.status !== PipelineRunStatus.PROCESSED ||
                            run.pipelineSyncStats.recordsFailed == 0,
                        })}
                        onClick={() => downloadErrorReport(run.id)}
                      >
                        <IconFileDownload size={18}/> Error Report
                      </Button>
                    </td>
                  </tr>
                </OverlayTrigger>
              ))}
            </tbody>
          </Table>
        </div>
      )}
    </>
  );
};

export default PipelineRunView;
