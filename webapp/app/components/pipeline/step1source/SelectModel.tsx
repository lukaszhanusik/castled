import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";
import WarehouseModel from "./WarehouseModel";
import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import { Col, Row } from "react-bootstrap";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";

const SelectModel = ({
  appBaseUrl,
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
}: PipelineWizardStepProps) => {
  const [models, setModels] = useState<ModelListDto[] | undefined | null>();
  const router = useRouter();
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();

  useEffect(() => {
    modelService
      .get()
      .then(({ data }) => {
        console.log(data);
        setModels(data);
      })
      .catch(() => {
        setModels(null);
      });
  }, []);

  const onModelSelect = (model: any) => {
    console.log(model);
  };

  if (models === null || (models && models.length === 0)) {
    return <p>No models found. Please create a model to continue.</p>;
  }
  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      {!models && <Loading />}
      {models && models.length > 0 && (
        <div className="categories">
          {models.map((model, idx) => (
            <button
              key={idx}
              className="btn list-group-item rounded model-item"
              onClick={() => onModelSelect(model)}
            >
              <Row>
                <Col className="col-md-2">
                  <img src="/images/sql-icon.svg" />
                </Col>
                <Col className="col-md-10 ps-0">
                  {model.modelName}
                  <div className="text-muted">
                    {model.modelDetails.sourceQuery}
                  </div>
                </Col>
              </Row>
            </button>
          ))}
        </div>
      )}
    </Layout>
  );
};

export default SelectModel;
