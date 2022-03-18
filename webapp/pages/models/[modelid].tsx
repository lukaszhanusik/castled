import { ModelTitle } from "./../../app/components/model/ModelTitle";
import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Tabs, Tab } from "react-bootstrap";
import modelService from "@/app/services/modelService";
import { GetServerSidePropsContext } from "next";
import routerUtils from "@/app/common/utils/routerUtils";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { NextRouter, useRouter } from "next/router";
import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";
import ModelQueryView from "@/app/components/model/ModelQueryView";
import ModelPipelineView from "@/app/components/model/ModelPipelineView";
import ModelColumnDetailsView from "@/app/components/model/ModelColumnDetailsView";
import PipelineWizardProvider, {
  usePipelineWizContext,
} from "@/app/common/context/pipelineWizardContext";
import { ConnectorTypeDto } from "@/app/common/dtos/ConnectorTypeDto";
import { AccessType } from "@/app/common/enums/AccessType";
import { PipelineCreateRequestDto } from "@/app/common/dtos/PipelineCreateRequestDto";

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
  const [model, setModel] = useState<ModelResponseDto | undefined | null>();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();

  useEffect(() => {
    modelService
      .getById(modelid)
      .then(({ data }) => {
        setModel(data);
      })
      .catch(() => {
        setModel(null);
      });
  }, []);

  if (model === null) return <DefaultErrorPage statusCode={404} />;

  return (
    <PipelineWizardProvider>
      <Layout
        title={renderTitle(model, router)}
        subTitle={undefined}
        pageTitle={model ? "Pipeline " + model.name : ""}
        rightBtn={{
          id: "create_pipeline_button",
          title: "Create Pipeline",
          isLoading: isLoading,
          onClick: () => {
            setIsLoading(true);
            if (model && pipelineWizContext) {
              const warehouseType: ConnectorTypeDto = {
                value: model.warehouse.type,
                title:
                  model.warehouse.type[0] +
                  model.warehouse.type.slice(1).toLowerCase(),
                accessType: AccessType.PASSWORD,
                logoUrl: model.warehouse.logoUrl,
                docUrl: "",
                count: 1,
              };

              const values: PipelineCreateRequestDto = {
                warehouseId: model.warehouse.id,
                modelId: model.id,
                sourceQuery: model.details.sourceQuery,
                appSyncConfig: {
                  appType: undefined,
                },
              };

              pipelineWizContext.warehouseType = warehouseType;
              pipelineWizContext.values = values;

              sessionStorage.setItem(
                "PIPELINE_WIZARD_PROVIDER",
                JSON.stringify(pipelineWizContext)
              );

              router.push("/pipelines/create?wizardStep=source:model");
            }
            // console.log(pipelineWizContext);
          },
        }}
      >
        {!model && <Loading />}
        <ModelTitle
          warehouse={model?.warehouse}
          activeSyncsCount={model?.activeSyncsCount}
        />
        <Tabs defaultActiveKey="Query" className="mb-3">
          <Tab eventKey="Query" title="Query">
            <ModelQueryView source={model?.details.sourceQuery} />
          </Tab>
          <Tab eventKey="Details" title="Column Details">
            <ModelColumnDetailsView
              columnDetails={model?.columnDetails}
              queryPK={model?.queryPK}
            />
          </Tab>
          <Tab eventKey="Pipelines" title="Pipelines">
            <ModelPipelineView
              activeSyncDetails={model ? model.activeSyncDetails : []}
              activeSyncsCount={model ? model.activeSyncsCount : 0}
            />
          </Tab>
        </Tabs>
      </Layout>
    </PipelineWizardProvider>
  );
};

function renderTitle(model: ModelResponseDto | undefined, router: NextRouter) {
  if (!model) return "";
  return (
    <>
      <div className="row">
        <div className="col-8">
          <span>{model.id} | </span> <span>{model.name}</span>
        </div>
        <div className="col-4">
          <button
            className="btn btn-outline-primary mx-2"
            onClick={() => {
              if (confirm("Are you sure you want to delete this model?")) {
                modelService
                  .delete(model.id)
                  .then(({ data }) => {
                    router.push("/models");
                  })
                  .catch(() => {
                    alert("Error deleting model. Please try again.");
                  });
              }
            }}
          >
            Delete Model
          </button>
        </div>
      </div>
    </>
  );
}

export default PipelineInfo;
