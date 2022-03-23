import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import formHandler from "@/app/common/utils/formHandler";
import React, { useState } from "react";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import Loading from "@/app/components/common/Loading";
import { ListGroup } from "react-bootstrap";
import _ from "lodash";
import { useSession } from "@/app/common/context/sessionContext";

const ModelType = ({
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
  onFinish,
}: PipelineWizardStepProps) => {
  const DEMO_QUERY = "SELECT * FROM USERS";
  const [demoQueries, setDemoQueries] = useState<string[] | undefined>();
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  if (!pipelineWizContext) return <Loading />;
  const [query, setQuery] = useState<string | undefined>(
    pipelineWizContext.isDemo ? DEMO_QUERY : undefined
  );
  const [warehouseId, setWarehouseId] = useState<any>(
    pipelineWizContext.values?.warehouseId
  );
  const { isOss } = useSession();

  const modelTypes = [
    {
      name: "Custom SQL Query",
      icon: "/images/sql-icon.svg",
    },
  ];

  const onModelTypeSelect = (type: any) => {
    setCurWizardStep("configure", "configureModel");
  };

  return (
    <div className="categories">
      <ListGroup>
        {modelTypes?.map((type, i) => (
          <ListGroup.Item
            key={i}
            className="list-group-item rounded animate"
            onClick={() => onModelTypeSelect(type)}
          >
            <img src={type.icon} />
            <strong>{type.name}</strong>
          </ListGroup.Item>
        ))}
      </ListGroup>
    </div>
  );
};

export default ModelType;
