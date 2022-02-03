import React, { useEffect, useState } from "react";
import Layout from "@/app/components/layout/Layout";
import { Alert, Badge, Table } from "react-bootstrap";
import pipelineService from "@/app/services/pipelineService";
import { PipelineResponseDto } from "@/app/common/dtos/PipelineResponseDto";
import Link from "next/link";
import DefaultErrorPage from "next/error";
import Loading from "@/app/components/common/Loading";
import { useRouter } from "next/router";

const Pipelines = () => {
  const [pipelines, setPipelines] = useState<
    PipelineResponseDto[] | undefined | null
  >();
  const headers = ["Name", "Source", "Destination", "Status"];
  const router = useRouter();
  useEffect(() => {
    pipelineService
      .get()
      .then(({ data }) => {
        setPipelines(data);
      })
      .catch(() => {
        setPipelines(null);
      });
  }, []);
  if (pipelines === null) return <DefaultErrorPage statusCode={404} />;
  if (pipelines && pipelines.length === 0) {
    router.push("/welcome");
    return (
      <Layout title="Loading Welcome..." subTitle={undefined} hideHeader={true}>
        <Loading />
      </Layout>
    );
  }
  return (
    <Layout
      title="Pipelines"
      subTitle={undefined}
      rightBtn={
        pipelines?.length
          ? {
              id: "create_pipeline_button",
              title: "Create",
              href: "/pipelines/create",
            }
          : undefined
      }
    >
      {!pipelines && <Loading />}
      {pipelines && pipelines.length > 0 && (
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
              {pipelines.map((pipeline, idx) => (
                <tr key={idx}>
                  <td>
                    <Link href={`/pipelines/${pipeline.id}`}>
                      <a>{pipeline.name}</a>
                    </Link>
                  </td>
                  <td>{pipeline.warehouse.name}</td>
                  <td>{pipeline.app.name}</td>
                  <td>
                    <Badge bg={pipeline.status === "OK" ? "success" : "danger"}>
                      {pipeline.status}
                    </Badge>
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

export default Pipelines;
