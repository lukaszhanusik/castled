import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Alert, Badge, OverlayTrigger, Table, Tooltip } from "react-bootstrap";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";

const Models = () => {
  const [models, setModels] = useState<ModelListDto[] | undefined | null>();
  const headers = ["Model Name", "Source", "Type", "Pipelines"];
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
  if (models === null) return <DefaultErrorPage statusCode={404} />;

  return (
    <Layout
      title="Model List"
      subTitle={undefined}
      rightBtn={{
        id: "create_model_button",
        title: "Create Model",
        href: "/models/create",
      }}
    >
      {!models && <Loading />}

      {models && models.length === 0 && (
        <p className="text-center">No models created yet.</p>
      )}

      {models && models.length > 0 && (
        <div className="table-responsive">
          <Table hover>
            <thead>
              <tr>
                {headers.map((header, idx) => (
                  <th key={idx}>{header}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {models.map((model, idx) => (
                <tr key={idx}>
                  <td>
                    <div className="w-50">
                      {model.modelName}

                      <div
                        className="text-muted"
                        style={{ whiteSpace: "nowrap" }}
                      >
                        <OverlayTrigger
                          placement="right"
                          key={`sidebar-${idx}`}
                          overlay={
                            <Tooltip id={`sidebar-${idx}`}>
                              {model.modelDetails.sourceQuery}
                            </Tooltip>
                          }
                        >
                          <div>
                            {model.modelDetails.sourceQuery.substring(0, 30)}{" "}
                            {model.modelDetails.sourceQuery.length >= 30 &&
                              `...`}
                          </div>
                        </OverlayTrigger>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div className="d-flex">
                      <img
                        src={model.warehouse.logoUrl}
                        alt={model.warehouse.name}
                        height={24}
                      />
                      <div className="ms-2">
                        {model.warehouse.name}
                        <div className="small text-muted">
                          {model.warehouse.type}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td>{model.modelType}</td>
                  <td>{model.activeSyncsCount}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      )}
    </Layout>
  );
};

export default Models;
