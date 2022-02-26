import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";
import WarehouseModel from "./WarehouseModel";
import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";

const SelectModel = ({
    appBaseUrl,
    curWizardStep,
    steps,
    stepGroups,
    setCurWizardStep,
  }: PipelineWizardStepProps)  => {
    const [models, setModels] = useState<ModelListDto[] | undefined | null>();
    const router = useRouter();
    useEffect(() => {
        modelService
            .get()
            .then(({ data }) => {
                setModels(data);
            })
            .catch(() => {
                setModels(null);
            });
    }, []);
    if (models === null || (models && models.length === 0)) {
        router.push("/welcome");
        return (
            <WarehouseModel
                appBaseUrl={appBaseUrl}
                curWizardStep={curWizardStep}
                steps={steps}
                stepGroups={stepGroups}
                setCurWizardStep={setCurWizardStep}
            />
        );
    }
    return (
        <Layout
            title="Model List"
            subTitle={undefined}
            rightBtn={
                models?.length
                    ? {
                        id: "create_model_button",
                        title: "Create Model",
                        href: "/models/create",
                    }
                    : undefined
            }
        >
            {!models && <Loading />}
            {models && models.length > 0 && (
                <ul>
                    <li>Create New Element</li>
                    {models.map((model, idx) => (
                        <li key={idx}>
                            <div className="d-flex">
                                <img
                                    src={model.warehouse.logoUrl}
                                    alt={model.warehouse.name}
                                    height={24}
                                />
                                <div className="ms-2">
                                    {model.modelName}
                                    <p className="small text-muted">
                                        {model.modelDetails.sourceQuery}
                                    </p>
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </Layout>
    );
};

export default SelectModel;
