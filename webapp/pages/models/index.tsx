import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Alert, Badge, Table } from "react-bootstrap";
import modelService from "@/app/services/modelService";
import { ModelListDto } from "@/app/common/dtos/ModelListDto";
import Link from "next/link";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";

const Models = () => {
  const [models, setModels] = useState<ModelListDto[] | undefined | null>();
  const headers = ["Model Name", "Source", "Type", "Sync"];
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
  if (models === null) return <DefaultErrorPage statusCode={404} />;
  if (models && models.length === 0) {
    router.push("/welcome");
    return (
      <Layout title="Loading..." subTitle={undefined} hideHeader={true}>
        <Loading />
      </Layout>
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
                    <Link href={`/models/${model.id}`}>
                      <a>{model.modelName}</a>
                    </Link>
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
                        <p className="small text-muted">
                          {model.warehouse.type}
                        </p>
                      </div>
                    </div>
                  </td>
                  <td>Sync</td>
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
