import React, { useEffect, useState } from "react";
import { ConnectorTypeDto } from "@/app/common/dtos/ConnectorTypeDto";
import { Badge, Col, ListGroup, Row } from "react-bootstrap";
import { ConnectorCategory } from "@/app/common/utils/types";
import appsService from "@/app/services/appsService";
import warehouseService from "@/app/services/warehouseService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import LoadingList from "../loaders/LoadingList";

export interface SelectConnectorTypeProps {
  category: ConnectorCategory;
  onSelect: (type: ConnectorTypeDto) => void;
}

const SelectConnectorType = ({
  category,
  onSelect,
}: SelectConnectorTypeProps) => {
  const [typeList, setTypeList] = useState<ConnectorTypeDto[] | undefined>();
  const { pipelineWizContext } = usePipelineWizContext();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetcher =
      category === "App" ? appsService.types : warehouseService.types;
    fetcher().then(({ data }) => {
      setLoading(false);
      setTypeList(data);
    });
  }, [category]);

  const selectType = (type: ConnectorTypeDto) => {
    if (category === "Model" && type.count === 0) {
      bannerNotificationService.warn(
        "Please setup a warehouse before proceeding to create a model."
      );
    } else {
      onSelect(type);
    }
  };

  return (
    <>
      {pipelineWizContext?.isDemo && category === "App" && (
        <div className="help-message">
          <Badge bg="warning" className="badge badge-warning">
            Google Sheets is the most used app for testing demo pipelines.
          </Badge>
        </div>
      )}
      <div className={category === "App" ? "grid-categories" : "categories"}>
        <Row md={category === "App" ? 3 : 12}>
          {loading &&
            <LoadingList n={category === "App" ? 14 : 4} />
          }
          {typeList?.map((type, i) => (
            <ListGroup key={i}>
              <ListGroup.Item
                className="rounded animate"
                onClick={() => selectType(type)}
              >
                <Col>
                  <div>
                    <img className={type.title} src={type.logoUrl}></img>
                    <strong>{type.title} </strong>
                    {type.count > 0 && (
                      <Badge bg="secondary">{type.count}</Badge>
                    )}
                  </div>
                </Col>
              </ListGroup.Item>
            </ListGroup>
          ))}
        </Row>
      </div>
    </>
  );
};

export default SelectConnectorType;
