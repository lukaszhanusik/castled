import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Alert, Badge, OverlayTrigger, Table, Tooltip } from "react-bootstrap";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import _ from "lodash";
import { IconChevronRight } from "@tabler/icons";

const Models = () => {
  const [models, setModels] = useState<ModelListDto[] | undefined | null>();
  const router = useRouter();
  const { setPipelineWizContext } = usePipelineWizContext();

  const headers = ["#", "Model name", "Source", "Type", "Pipelines"];
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
  console.log(models);
  return (
    <Layout
      title="Models"
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
          <Table className="tr-collapse">
            <thead>
              <tr>
                {headers.map((header, idx) => (
                  <th key={idx}>{header}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {models.map((model, idx) => (
                <tr
                  className="cursor-pointer"
                  key={idx}
                  onClick={() =>
                    router
                      .push(`/models/${model.id}`)
                      .then(() => setPipelineWizContext({}))
                  }
                >
                  <td>{model.id}</td>
                  <td>
                    <div>
                      <span>{model.name}</span>

                      <div
                        className="text-muted w-75"
                        style={{ whiteSpace: "nowrap" }}
                      >
                        <OverlayTrigger
                          placement="right"
                          key={`sidebar-${idx}`}
                          overlay={
                            <Tooltip id={`sidebar-${idx}`}>
                              {model.details.sourceQuery}
                            </Tooltip>
                          }
                        >
                          <div>
                            {model.details.sourceQuery.substring(0, 30)}{" "}
                            {model.details.sourceQuery.length >= 30 && `...`}
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
                        className="mt-1"
                      />
                      <div className="ms-2">
                        <span>{model.warehouse.name}</span>
                        {/* <div className="small text-muted">
                          {_.capitalize(model.warehouse.type)}
                        </div> */}
                      </div>
                    </div>
                  </td>
                  <td>
                    <div className="d-flex">
                      <img
                        src="/images/sql-icon.svg"
                        height={24}
                        className="mt-1 me-2"
                      />
                      {model.type} model
                    </div>
                  </td>
                  <td>
                    <span className="badge text-dark fs-4">
                      {model.activeSyncsCount}
                    </span>
                    <IconChevronRight className="float-end me-2 text-secondary" />
                  </td>
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
