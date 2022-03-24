import React from "react";
import Layout from "@/app/components/layout/Layout";
import { ConnectorTypeDto } from "@/app/common/dtos/ConnectorTypeDto";
import ConnectorForm from "@/app/components/connectors/ConnectorForm";
import { WizardSteps } from "@/app/common/dtos/internal/WizardSteps";
import SelectConnectorType from "@/app/components/connectors/SelectConnectorType";
import { ConnectorCategory } from "@/app/common/utils/types";
import SelectExistingConnector from "@/app/components/connectors/SelectExistingConnector";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import Loading from "@/app/components/loaders/Loading";
import _ from "lodash";
import IntegratedDoc from "../layout/IntegratedDoc";
import ConnectorHelpSubTitle from "./ConnectorHelpSubTitle";
import SelectModel from "../pipeline/step1source/SelectModel";
import ModelType from "@/app/components/connectors/ModelType";
import { removeAllLocalStorageMapping } from "../pipeline/step3mapping/utils/MappingAutoFill";

interface ConnectorWizardProps {
  appBaseUrl: string;
  category: ConnectorCategory;
  curWizardStepGroup: string | undefined;
  curWizardStep: string;
  steps: WizardSteps;
  stepGroups?: WizardSteps;
  oauthCallback?: string;
  setCurWizardStep: (stepGroup: string | undefined, step: string) => void;
  onConnectorTypeSelect?: (type: ConnectorTypeDto) => void;
  onFinish: (id: number) => void;
}

const ConnectorWizard = ({
  appBaseUrl,
  category,
  curWizardStepGroup,
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
  onConnectorTypeSelect,
  oauthCallback,
  onFinish,
}: ConnectorWizardProps) => {
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  if (!pipelineWizContext) return <Loading />;
  const typeOption =
    category === "App"
      ? pipelineWizContext.appType
      : pipelineWizContext.warehouseType;
  if (curWizardStep === "configure" && !typeOption?.value) {
    setCurWizardStep(undefined, "selectType");
    return null;
  }
  const saveTypeOption = (type: ConnectorTypeDto) => {
    if (category === "App") {
      pipelineWizContext.appType = type;
      _.set(pipelineWizContext, "mappingInfo.appType", type.value);
    } else {
      pipelineWizContext.warehouseType = type;
    }
    setPipelineWizContext(pipelineWizContext);
  };
  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={
        <ConnectorHelpSubTitle
          description={steps[curWizardStep].description}
          curWizardStep={curWizardStep}
          docUrl={typeOption?.docUrl}
        />
      }
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
      rightHelp={
        curWizardStep === "configure" &&
        typeOption &&
        process.env.INTEGRATED_DOC ? (
          <IntegratedDoc category={category} connectorType={typeOption.value} />
        ) : undefined
      }
    >
      <>
        {curWizardStep === "selectType" && (
          <SelectConnectorType
            category={category}
            onSelect={(type) => {
              onConnectorTypeSelect?.(type);
              saveTypeOption(type);
              setCurWizardStep(
                curWizardStepGroup,
                "selectExisting" in steps && type.count > 0
                  ? "selectExisting"
                  : "configure"
              );
            }}
          />
        )}
        {curWizardStep === "selectExisting" && typeOption && (
          <SelectExistingConnector
            category={category}
            typeOption={typeOption}
            onCreate={() => {
              setCurWizardStep(curWizardStepGroup, "configure");
            }}
            onSelect={(id) => {
              removeAllLocalStorageMapping();
              if (category === "App") {
                onFinish(id);
              } else {
                _.set(pipelineWizContext, "values.warehouseId", id);
                setPipelineWizContext(pipelineWizContext);
                if (category !== "Model") {
                  setCurWizardStep(curWizardStepGroup, "selectModelType");
                } else {
                  setCurWizardStep(curWizardStepGroup, "modelType");
                }
              }
            }}
          />
        )}
        {curWizardStep === "selectModelType" && (
          <SelectModel
            category={category}
            onCreate={() => {
              setCurWizardStep(curWizardStepGroup, "model");
            }}
            onSelect={(id: number, sourceQuery: string, warehouseId: number) => {
              if (category !== "Model") {
                _.set(pipelineWizContext, "values.modelId", id);
                _.set(pipelineWizContext, "values.sourceQuery", sourceQuery);
                _.set(pipelineWizContext, "values.warehouseId", warehouseId);
                setPipelineWizContext(pipelineWizContext);
                setCurWizardStep(curWizardStepGroup, "model");
              } else {
                onFinish(id);
              }
            }}
          />
        )}
        {curWizardStep === "modelType" && category == "Model" && (
          <ModelType
            appBaseUrl={appBaseUrl}
            curWizardStep={curWizardStep}
            steps={steps}
            setCurWizardStep={setCurWizardStep}
          ></ModelType>
        )}
        {curWizardStep === "configure" && typeOption && (
          <ConnectorForm
            appBaseUrl={appBaseUrl}
            oauthCallback={oauthCallback}
            category={category}
            connectorType={typeOption.value}
            accessType={typeOption.accessType}
            onFinish={onFinish}
          />
        )}
      </>
    </Layout>
  );
};

export default ConnectorWizard;
