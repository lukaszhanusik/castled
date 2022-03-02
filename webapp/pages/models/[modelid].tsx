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
  Col,
  Row,
} from "react-bootstrap";
import modelService from "@/app/services/modelService";
import PipelineRunView from "@/app/components/pipeline/PipelineRunView";
import PipelineMappingView from "@/app/components/pipeline/PipelineMappingView";
import PipelineMappingViewRestApi from "@/app/components/pipeline/PipelineMappingViewRestApi";
import { PipelineResponseDto } from "@/app/common/dtos/PipelineResponseDto";
import { PipelineResponseRestApiDto } from "@/app/common/dtos/PipelineResponseRestApiDto";
import { GetServerSidePropsContext } from "next";
import routerUtils from "@/app/common/utils/routerUtils";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import pipelineRunsService from "@/app/services/pipelineRunsService";
import { PipelineRunDto } from "@/app/common/dtos/PipelineRunDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { IconArrowRight, IconDots } from "@tabler/icons";
import Image from "react-bootstrap/Image";
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
import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";

export async function getServerSideProps({ query }: GetServerSidePropsContext) {
  const modelid = routerUtils.getInt(query.modelid);
  return {
    props: { modelid },
  };
}

interface ModelInfoProps {
  modelid: number;
}

const PipelineInfo = ({ modelid }: ModelInfoProps) => {
  const router = useRouter();
  const MAX_RELOAD_COUNT = 100;
  const [reloadKey, setReloadKey] = useState<number>(0);
  const [reloadCount, setReloadCount] = useState<number>(0);

  const [model, setModel] = useState<ModelResponseDto | undefined | null>();

  const [isLoading, setIsLoading] = useState<boolean>(false);

  useEffect(() => {
    modelService
      .getById(modelid)
      .then(({ data }) => {
        setModel(data);
      })
      .catch(() => {
        setModel(null);
      });
  }, [reloadKey]);

  if (model === null) return <DefaultErrorPage statusCode={404} />;

  console.log(model);
  return (
    <Layout
      title={renderTitle(model, router, setModel, setReloadKey)}
      subTitle={undefined}
      pageTitle={model ? "Pipeline " + model.modelName : ""}
      rightBtn={{
        id: "create_pipeline_button",
        title: "Create Pipeline",
        isLoading: isLoading,
        onClick: () => {
          setIsLoading(true);
          // pipelineService.triggerRun(pipelineId).then(() => {
          //   setReloadKey(Math.random());
          //   bannerNotificationService.success("Triggered Run");
          // });
        },
      }}
    >
      {!model && <Loading />}
      <div className="categories">
        <Row>
          <Col className="col-md-2">
            <img src={model?.warehouse.logoUrl} />
          </Col>
          <Col className="col-md-10 ps-0">
            {model?.warehouse.type}
            <div className="text-muted">{model?.modelType}</div>
          </Col>
        </Row>
      </div>
      <Tabs defaultActiveKey="Query" className="mb-3">
        <Tab eventKey="Query" title="Query">
          {/* <PipelineRunView pipelineRuns={pipelineRuns}></PipelineRunView> */}
        </Tab>
        <Tab eventKey="Details" title="Column Details">
          {/* {(() => {
            if (pipeline && pipeline.app && pipeline.app.type == "RESTAPI") {
              return (
                <PipelineMappingViewRestApi
                  sourceQuery={pipelineRestApi?.sourceQuery}
                  dataMapping={pipelineRestApi?.dataMapping}
                ></PipelineMappingViewRestApi>
              );
            } else {
              return (
                <PipelineMappingView
                  sourceQuery={pipeline?.sourceQuery}
                  dataMapping={pipeline?.dataMapping}
                ></PipelineMappingView>
              );
            }
          })()} */}
        </Tab>
        <Tab eventKey="Schedule" title="Schedule">
          {/* <PipelineSettingsView
            key={pipeline?.id}
            pipelineId={pipeline?.id}
            name={pipeline?.name}
            schedule={pipeline?.jobSchedule}
            queryMode={pipeline?.queryMode}
          ></PipelineSettingsView> */}
        </Tab>
      </Tabs>
    </Layout>
  );
};

function renderTitle(
  model: ModelResponseDto | undefined,
  router: NextRouter,
  setModel: (value: any) => void,
  setReloadKey: (value: any) => void
) {
  if (!model) return "";
  // const isActive = model.syncStatus === PipelineSyncStatus.ACTIVE;
  return (
    <>
      <span>{model.id} | </span> <span>{model.modelName}</span>
      <div className="float-end">
        <button
          className="btn btn-outline-primary mx-2"
          // onClick={() => {
          //   pipelineRunsService
          //     .getByPipelineId(pipeline.id)
          //     .then(({ data }) => {
          //       setPipelineRuns(data);
          //     })
          //     .catch(() => {
          //       setPipelineRuns(null);
          //     });
          // }}
        >
          Delete Model
        </button>
      </div>
    </>
  );
}

export default PipelineInfo;
