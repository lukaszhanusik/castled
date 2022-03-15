import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Form, Table } from "react-bootstrap";
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
import { IconArrowNarrowRight, IconChevronRight } from "@tabler/icons";
const Pipelines = () => {
  const [pipelines, setPipelines] = useState<
    PipelineResponseDto[] | undefined | null
  >();
  const headers = [
    "#",
    "Pipeline Name",
    "Model",
    "Destination",
    "Frequency",
    "Enabled",
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
    pipeline: PipelineResponseDto,
    isActive: boolean
  ) => {
    {
      const pipelineNew = _.cloneDeep(pipeline);
      if (isActive) {
        pipelineService.pause(pipeline.id).then(() => {
          if (pipeline) {
            pipelineNew.syncStatus = PipelineSyncStatus.PAUSED;
            setPipelines([
              ...pipelines.filter((x) => x.id !== pipeline.id),
              pipelineNew,
            ]);
          }
          bannerNotificationService.success("Pipeline Paused");
        });
      } else {
        pipelineService.resume(pipeline.id).then(() => {
          if (pipeline) {
            pipelineNew.syncStatus = PipelineSyncStatus.ACTIVE;
            setPipelines([
              ...pipelines.filter((x) => x.id !== pipeline.id),
              pipelineNew,
            ]);
          }
          bannerNotificationService.success("Pipeline Resumed");
        });
      }
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
              title: "Create",
              href: "/pipelines/create",
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
                      {/* <img
                        src={`/images/${status}.svg`}
                        width={14}
                        className="me-2"
                      /> */}
                      <Link href={`/pipelines/${pipeline.id}`}>
                        <span className="fw-bolder">{pipeline.name}</span>
                      </Link>
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      <div className="d-flex">
                        <img
                          src={pipeline.warehouse.logoUrl}
                          alt={pipeline.warehouse.name}
                          height={24}
                          className="mt-1"
                        />
                        <div className="ms-2">
                          <span>{pipeline.warehouse.name}</span>
                          <p className="text-muted mb-0 small">
                            {_.capitalize(pipeline.warehouse.type)}
                          </p>
                        </div>
                        <IconArrowNarrowRight
                          className={`text-${status} ms-5`}
                        />
                      </div>
                    </td>
                    <td
                      onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                    >
                      <div className="d-flex">
                        <img
                          src={pipeline.app.logoUrl}
                          alt={pipeline.app.name}
                          height={24}
                          className="mt-1"
                        />
                        <div className="ms-2">
                          <span>{pipeline.app.name}</span>
                          <p className="text-muted mb-0 small">
                            {_.capitalize(pipeline.app.type)}
                          </p>
                        </div>
                      </div>
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
                          enabledHandler(pipelines, pipeline, isActive)
                        }
                      />
                      <IconChevronRight
                        className="float-end me-2 text-secondary"
                        onClick={() => router.push(`/pipelines/${pipeline.id}`)}
                      />
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
