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
import { ModelSyncView } from "@/app/components/model/ModelSyncView";
import ModelColumnDetailsView from "@/app/components/model/ModelColumnDetailsView";

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
  const [reloadKey, setReloadKey] = useState<number>(0);
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

  return (
    <Layout
      title={renderTitle(model, router)}
      subTitle={undefined}
      pageTitle={model ? "Pipeline " + model.modelName : ""}
      rightBtn={{
        id: "create_pipeline_button",
        title: "Create Pipeline",
        isLoading: isLoading,
        onClick: () => {
          setIsLoading(true);
        },
      }}
    >
      {!model && <Loading />}
      <div className="categories m-0">
        <img src={model?.warehouse.logoUrl} />
        <span className="col-md-10 ps-0 px-4 font-weight-bold">
          {model?.warehouse.name}
        </span>
        <span className="col-md-10 ps-0 px-4 text-muted">
          {model?.warehouse.type}
        </span>
        <span className="col-md-10 ps-0 px-4">
          Total Syncs - {model?.activeSyncsCount}
        </span>
      </div>
      <Tabs defaultActiveKey="Query" className="mb-3">
        <Tab eventKey="Query" title="Query">
          <ModelQueryView source={model?.modelDetails.sourceQuery} />
        </Tab>
        <Tab eventKey="Details" title="Column Details">
          <ModelColumnDetailsView
            warehouse={model?.warehouse}
            queryModelPK={model?.queryModelPK}
          />
        </Tab>
        <Tab eventKey="Syncs" title="Syncs">
          <ModelSyncView
            activeSyncDetails={model ? model.activeSyncDetails : []}
            activeSyncsCount={model ? model.activeSyncsCount : 0}
          />
        </Tab>
      </Tabs>
    </Layout>
  );
};

function renderTitle(
  model: ModelResponseDto | undefined,
  router: NextRouter,
) {
  if (!model) return "";
  return (
    <>
      <span>{model.id} | </span> <span>{model.modelName}</span>
      <div className="float-end">
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
    </>
  );
}

export default PipelineInfo;
