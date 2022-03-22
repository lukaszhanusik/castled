import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { OverlayTrigger, Tooltip, Form, Table } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import { PipelineResponseDto } from "@/app/common/dtos/PipelineResponseDto";
import Link from "next/link";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";
import PipelineScheduleUtils from "@/app/common/utils/pipelineScheduleUtils";
import { SchedulTimeUnitLabel } from "@/app/common/enums/ScheduleType";
import _ from "lodash";
import { PipelineSyncStatus } from "@/app/common/enums/PipelineSyncStatus";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { IconChevronRight } from "@tabler/icons";

const Pipelines = () => {
  const [pipelines, setPipelines] = useState<
    PipelineResponseDto[] | undefined | null
  >();
  const headers = [
    "#",
    "Name",
    "Model",
    "Destination",
    "Frequency",
    "Enabled",
    "",
  ];
  const router = useRouter();
  useEffect(() => {
    pipelineService
      .get()
      .then(({ data }) => {
        setPipelines(data);
      })
      .catch(() => {
        setPipelines(null);
      });
  }, []);
  if (pipelines === null) return <DefaultErrorPage statusCode={404} />;
  if (pipelines && pipelines.length === 0) {
    router.push("/welcome");
    return (
      <Layout title="Loading Welcome..." subTitle={undefined} hideHeader={true}>
        <Loading />
      </Layout>
    );
  }
  const enabledHandler = (
    pipelines: PipelineResponseDto[],
    idx: number,
    isActive: boolean
  ) => {
    const pipeline = pipelines[idx];
    const pipelineNew = _.cloneDeep(pipeline);
    if (isActive) {
      pipelineService.pause(pipeline.id).then(() => {
        if (pipeline) {
          pipelineNew.syncStatus = PipelineSyncStatus.PAUSED;
          let pipelinesNew = [...pipelines];
          pipelinesNew[idx] = pipelineNew;
          setPipelines(pipelinesNew);
        }
        bannerNotificationService.success("Pipeline Paused");
      });
    } else {
      pipelineService.resume(pipeline.id).then(() => {
        if (pipeline) {
          pipelineNew.syncStatus = PipelineSyncStatus.ACTIVE;
          let pipelinesNew = [...pipelines];
          pipelinesNew[idx] = pipelineNew;
          setPipelines(pipelinesNew);
        }
        bannerNotificationService.success("Pipeline Resumed");
      });
    }
  };
  return (
    <Layout
      title="Pipelines"
      subTitle={undefined}
      rightBtn={
        pipelines?.length
          ? {
              id: "create_pipeline_button",
              title: "Create Pipeline",
              href: "/pipelines/create?wizardStep=source:selectModelType",
            }
          : undefined
      }
    >
      {!pipelines && <Loading />}
      {pipelines && pipelines.length > 0 && (
        <div className="table-responsive">
          <Table className="tr-collapse">
            <thead>
              <tr>
                {headers.map((header, idx) => (
                  <th key={idx}>{header}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {pipelines.map((pipeline, idx) => {
                const jobSchedule = PipelineScheduleUtils.getSettingsSchedule(
                  pipeline.jobSchedule
                );
                const isActive =
                  pipeline.syncStatus === PipelineSyncStatus.ACTIVE;

                let status;
                if (pipeline.status === "OK") {
                  if (pipeline.syncStatus === "ACTIVE") status = "success";
                  else status = "warning";
                } else status = "danger";

                return (
                  <tr key={idx}>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      {pipeline.id}
                    </td>
                    <td>
                      <Link href={`/pipelines/${pipeline.id}`}>
                        <span>{pipeline.name}</span>
                      </Link>
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      <OverlayTrigger
                        placement="right"
                        key={pipeline.warehouse.name}
                        overlay={
                          <Tooltip id={pipeline.warehouse.name}>
                            {_.capitalize(pipeline.warehouse.type)}
                          </Tooltip>
                        }
                      >
                        <div className="d-flex">
                          <img
                            src={pipeline.warehouse.logoUrl}
                            alt={pipeline.warehouse.name}
                            height={24}
                            className="mt-1"
                          />
                          <div className="ms-2">{pipeline.warehouse.name}</div>
                        </div>
                      </OverlayTrigger>
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      <OverlayTrigger
                        placement="right"
                        key={pipeline.app.name}
                        overlay={
                          <Tooltip id={pipeline.app.name}>
                            {_.capitalize(pipeline.app.type)}
                          </Tooltip>
                        }
                      >
                        <div
                          className="d-flex"
                          data-toggle="tooltip"
                          data-placement="right"
                          title={_.capitalize(pipeline.app.type)}
                        >
                          <img
                            src={pipeline.app.logoUrl}
                            alt={pipeline.app.name}
                            height={24}
                            className="mt-1"
                          />
                          <div className="ms-2">{pipeline.app.name}</div>
                        </div>
                      </OverlayTrigger>
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      {jobSchedule.frequency}{" "}
                      {SchedulTimeUnitLabel[jobSchedule.timeUnit!]}
                    </td>
                    <td className="align-middle">
                      <Form.Check
                        className="d-inline-block"
                        type="switch"
                        id="pipeline-switch"
                        checked={isActive}
                        onChange={() =>
                          enabledHandler(pipelines, idx, isActive)
                        }
                      />
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      <IconChevronRight className="float-end me-2 text-secondary" />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </div>
      )}
    </Layout>
  );
};

export default Pipelines;
