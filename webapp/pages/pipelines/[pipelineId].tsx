import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import {
  Badge,
  Dropdown,
  Form,
  OverlayTrigger,
  Tooltip,
  Tabs,
  Tab,
} from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import cn from "classnames";
import PipelineRunView from "@/app/components/pipeline/PipelineRunView";
import PipelineMappingView from "@/app/components/pipeline/PipelineMappingView";
import { PipelineResponseDto } from "@/app/common/dtos/PipelineResponseDto";
import { GetServerSidePropsContext } from "next";
import routerUtils from "@/app/common/utils/routerUtils";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import pipelineRunsService from "@/app/services/pipelineRunsService";
import { PipelineRunDto } from "@/app/common/dtos/PipelineRunDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { IconArrowRight, IconDots } from "@tabler/icons";
import {
  PipelineSyncStatus,
  PipelineSyncStatusLabel,
} from "@/app/common/enums/PipelineSyncStatus";
import _ from "lodash";
import DropdownPlain from "@/app/components/bootstrap/DropdownPlain";
import { NextRouter, useRouter } from "next/router";
import PipelineSettingsView from "@/app/components/pipeline/PipelineSettingsView";
import { ScheduleType } from "@/app/common/enums/ScheduleType";
import { PipelineRunStatus } from "@/app/common/enums/PipelineRunStatus";
import TimeAgo from "react-timeago";

export async function getServerSideProps({ query }: GetServerSidePropsContext) {
  const pipelineId = routerUtils.getInt(query.pipelineId);
  return {
    props: { pipelineId },
  };
}

interface PipelineInfoProps {
  pipelineId: number;
}

const PipelineInfo = ({ pipelineId }: PipelineInfoProps) => {
  const router = useRouter();
  const MAX_RELOAD_COUNT = 100;
  const [reloadKey, setReloadKey] = useState<number>(0);
  const [reloadCount, setReloadCount] = useState<number>(0);
  const [recordSyncStatus, setRecordSyncStatus] = useState<PipelineRunStatus>(
    PipelineRunStatus.PROCESSING
  );
  const [pipeline, setPipeline] = useState<
    PipelineResponseDto | undefined | null
  >();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [pipelineRuns, setPipelineRuns] = useState<
    PipelineRunDto[] | undefined | null
  >();
  useEffect(() => {
    pipelineService
      .getById(pipelineId)
      .then(({ data }) => {
        setPipeline(data);
      })
      .catch(() => {
        setPipeline(null);
      });
    pipelineRunsService
      .getByPipelineId(pipelineId)
      .then(({ data }) => {
        setPipelineRuns(data);
        if (data.length > 0) {
          setRecordSyncStatus(data[0].status);
        }
        if (
          data.length > 0 &&
          data[0].status === PipelineRunStatus.PROCESSING &&
          reloadCount < MAX_RELOAD_COUNT
        ) {
          setTimeout(() => {
            setReloadCount(reloadCount + 1);
            setReloadKey(reloadKey + 1);
          }, 2000);
        } else if (data.length === 0) {
          // We might to be too early after a pipeline creation, retry!
          setTimeout(() => {
            setReloadCount(reloadCount + 1);
            setReloadKey(reloadKey + 1);
          }, 1000);
        }
        setIsLoading(false);
      })
      .catch((error) => {
        console.log(JSON.stringify(error.message));
        setPipelineRuns(null);
      });
  }, [reloadKey]);

  if (pipeline === null) return <DefaultErrorPage statusCode={404} />;
  return (
    <Layout
      title={renderTitle(pipeline, router, setPipeline, setReloadKey)}
      subTitle={undefined}
      pageTitle={pipeline ? "Pipeline " + pipeline.name : ""}
      rightBtn={{
        id: "sync_pipeline_button",
        title: "Sync Now",
        isLoading: isLoading,
        onClick: () => {
          setIsLoading(true);
          pipelineService.triggerRun(pipelineId).then(() => {
            setReloadKey(Math.random());
            bannerNotificationService.success("Triggered Run");
          });
        },
      }}
    >
      {!pipeline && <Loading />}
      {pipeline && (
        <div className="mb-4">
          <span>{pipeline.warehouse.name}</span>
          <IconArrowRight className="ms-2 me-2" />
          <span>{pipeline.app.name}</span>
        </div>
      )}
      {pipelineRuns && recordSyncStatus === PipelineRunStatus.PROCESSING && (
        <div className="card p-2 mb-2 bg-light">
          <h2>Waiting for data to sync..</h2>
          <p>This may take some time</p>
        </div>
      )}
      {pipelineRuns && recordSyncStatus === PipelineRunStatus.PROCESSED && (
        <div className="card p-2 mb-2 bg-light">
          <h2>Data sync successful!
          {pipelineRuns[0].createdTs && (
            <TimeAgo date={pipelineRuns[0].createdTs} minPeriod={10} />
          )}
          </h2>
          <p>
            Go to <strong>{pipeline?.app.name}</strong> to check the data synced
          </p>
        </div>
      )}
      {pipelineRuns && recordSyncStatus === PipelineRunStatus.FAILED && (
        <div className="card p-2 mb-2 bg-light">
          <h2>Data sync failed!</h2>
          <p>
            Go to <strong>{pipeline?.app.name}</strong> to check the data synced
          </p>
        </div>
      )}
      <Tabs defaultActiveKey="Runs" className="mb-3">
        <Tab eventKey="Runs" title="Runs">
          <PipelineRunView pipelineRuns={pipelineRuns}></PipelineRunView>
        </Tab>
        <Tab eventKey="Mapping" title="Query & Mapping">
          <PipelineMappingView
            sourceQuery={pipeline?.sourceQuery}
            dataMapping={pipeline?.dataMapping}
          ></PipelineMappingView>
        </Tab>
        <Tab eventKey="Schedule" title="Schedule">
          <PipelineSettingsView
            key={pipeline?.id}
            pipelineId={pipeline?.id}
            name={pipeline?.name}
            schedule={pipeline?.jobSchedule}
            queryMode={pipeline?.queryMode}
          ></PipelineSettingsView>
        </Tab>
      </Tabs>
    </Layout>
  );
};

function renderTitle(
  pipeline: PipelineResponseDto | undefined,
  router: NextRouter,
  setPipeline: (value: any) => void,
  setReloadKey: (value: any) => void
) {
  if (!pipeline) return "";
  const isActive = pipeline.syncStatus === PipelineSyncStatus.ACTIVE;
  return (
    <>
      <span>#{pipeline.id} | </span> <span>{pipeline.name}</span>
      <span className="ms-2">
        <Badge bg={pipeline.status === "OK" ? "success" : "danger"}>
          {pipeline.status}
        </Badge>
      </span>
      <OverlayTrigger
        placement="right"
        key={`pipeline-sync-status-${pipeline.id}`}
        overlay={
          <Tooltip
            id={`pipeline-sync-status-${pipeline.id}`}
            className="tooltip"
          >
            Pipeline is {PipelineSyncStatusLabel[pipeline.syncStatus]}
          </Tooltip>
        }
      >
        <Form.Check
          className="d-inline-block ms-4"
          type="switch"
          id="pipeline-switch"
          checked={isActive}
          onChange={() => {
            const pipelineNew = _.cloneDeep(pipeline);
            if (isActive) {
              pipelineService.pause(pipeline.id).then(() => {
                if (pipeline) {
                  pipelineNew.syncStatus = PipelineSyncStatus.PAUSED;
                  setPipeline(pipelineNew);
                }
                bannerNotificationService.success("Pipeline Paused");
              });
            } else {
              pipelineService.resume(pipeline.id).then(() => {
                if (pipeline) {
                  pipelineNew.syncStatus = PipelineSyncStatus.ACTIVE;
                  setPipeline(pipelineNew);
                }
                bannerNotificationService.success("Pipeline Resumed");
              });
            }
          }}
        />
      </OverlayTrigger>
      <Dropdown align="end" className="d-inline-block float-end me-4">
        <Dropdown.Toggle as={DropdownPlain} id="dropdown-custom-components">
          <IconDots />
        </Dropdown.Toggle>
        <Dropdown.Menu align="end">
          <Dropdown.Item
            onClick={() => {
              pipelineService.restart(pipeline.id).then(() => {
                setReloadKey(Math.random());
                bannerNotificationService.success("Pipeline Restarted");
              });
            }}
          >
            Restart
          </Dropdown.Item>
          <Dropdown.Item
            onClick={() => {
              pipelineService.delete(pipeline.id).then(() => {
                bannerNotificationService.success("Pipeline Deleted");
                router.push("/pipelines").then();
              });
            }}
          >
            Delete
          </Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>
    </>
  );
}

export default PipelineInfo;
