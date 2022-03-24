import React, { useEffect, useState } from "react";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import DefaultErrorPage from "next/error";
import { useRouter } from "next/router";
import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import {
  Col,
  Row,
  Alert,
  Badge,
  OverlayTrigger,
  Table,
  Tooltip,
} from "react-bootstrap";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import { ConnectorTypeDto } from "@/app/common/dtos/ConnectorTypeDto";
import _ from "lodash";
import { ConnectorCategory } from "@/app/common/utils/types";
import { removeAllLocalStorageMapping } from "../step3mapping/utils/MappingAutoFill";
import LoadingConnector from "../../loaders/LoadingConnector";
export interface SelectExistingConnectorProps {
  category: ConnectorCategory;
  onCreate: () => void;
  onSelect: (id: number, sourceQuery: string) => void;
  typeOption: ConnectorTypeDto;
}

const SelectModel = ({
  category,
  onCreate,
  onSelect,
  typeOption,
}: SelectExistingConnectorProps) => {
  const [models, setModels] = useState<ModelListDto[] | undefined | null>();
  const router = useRouter();
  const { pipelineWizContext, setPipelineWizContext } = usePipelineWizContext();
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    modelService
      .get(pipelineWizContext?.values?.warehouseId)
      .then(({ data }) => {
        setLoading(false);
        setModels(data);
      })
      .catch(() => {
        setModels(null);
      });
  }, []);

  const onModelSelect = (model: ModelListDto) => {
    onSelect(model.id, model.details.sourceQuery);
    removeAllLocalStorageMapping();
  };

  const goToModelCreate = () => {
    router.push("/models/create");
  };

  if (models === null || (models && models.length === 0)) {
    return (
      <div className="text-center">
        <p>No models found. Please create a model to continue.</p>
        <button className="btn btn-primary" onClick={goToModelCreate}>
          Create Model
        </button>
      </div>
    );
  }
  return (
    <div>
      {loading &&
        <div className="categories">
          <div className="row">
            <LoadingConnector />
          </div>
        </div>}
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
                  {model.name}
                  <OverlayTrigger
                    placement="right"
                    key={`sidebar-${idx}`}
                    overlay={
                      <Tooltip id={`sidebar-${idx}`}>
                        {model.details.sourceQuery}
                      </Tooltip>
                    }
                  >
                    <div className="text-muted">
                      {model.details.sourceQuery}
                    </div>
                  </OverlayTrigger>
                </Col>
              </Row>
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default SelectModel;
