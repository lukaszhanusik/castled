import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import { Form, Formik } from "formik";
import formHandler from "@/app/common/utils/formHandler";
import React, { useEffect, useState } from "react";
import warehouseService from "@/app/services/warehouseService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import Loading from "@/app/components/common/Loading";
import { ExecuteQueryRequestDto } from "@/app/common/dtos/ExecuteQueryRequestDto";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { ExecuteQueryResultsDto } from "@/app/common/dtos/ExecuteQueryResultsDto";
import { Table } from "react-bootstrap";
import _ from "lodash";
import InputField from "@/app/components/forminputs/InputField";
import { Button } from "react-bootstrap";
import { useSession } from "@/app/common/context/sessionContext";
import { IconChevronRight, IconLoader, IconPlayerPlay } from "@tabler/icons";
import * as yup from "yup";

const WarehouseModel = ({
  curWizardStep,
  steps,
  stepGroups,
  setCurWizardStep,
}: PipelineWizardStepProps) => {
  const [queryResults, setQueryResults] = useState<
    ExecuteQueryResultsDto | undefined
  >();
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

  const updateDemoQueries = (whId: number) => {
    warehouseService.demoQueries(whId).then(({ data }) => {
      setDemoQueries(data);
    });
  };
  useEffect(() => {
    if (!pipelineWizContext) return;
    if (pipelineWizContext.isDemo) {
      warehouseService.get().then(({ data }) => {
        const demoWarehouseId = data.find((d) => d.demo)?.id;
        if (!demoWarehouseId) {
          setCurWizardStep("source", "selectType");
        } else {
          getDemoQuery(demoWarehouseId);
          setWarehouseId(demoWarehouseId);
          _.set(pipelineWizContext, "values.warehouseId", demoWarehouseId);
          setPipelineWizContext(pipelineWizContext);
        }
      });
    } else if (!warehouseId) {
      setCurWizardStep("source", "selectType");
    } else {
      setWarehouseId(pipelineWizContext.values?.warehouseId);
    }
  }, [
    !!pipelineWizContext,
    warehouseId,
    pipelineWizContext.values?.warehouseId,
  ]);
  const getDemoQuery = async (warehouseId: number) => {
    updateDemoQueries(warehouseId!);
  };
  const getQueryResults = (queryId: string) => {
    warehouseService
      .executeQueryResults(queryId)
      .then(({ data }) => {
        if (data.status === "PENDING") {
          setTimeout(() => getQueryResults(queryId), 1000);
        }
        setQueryResults(data);
      })
      .catch(() => {
        bannerNotificationService.error("Query failed unexpectedly");
      });
  };
  const nextStep = (): void => {
    if (!query) {
      bannerNotificationService.error("Please enter a query");
      return;
    }
    _.set(pipelineWizContext, "values.sourceQuery", query);
    setPipelineWizContext(pipelineWizContext);
    setCurWizardStep("destination", "selectType");
  };
  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      stepGroups={stepGroups}
    >
      {!!demoQueries?.length && (
        <p className="mb-1">
          Run the prefilled query below for the demo warehouse.
        </p>
      )}
      <Formik
        initialValues={
          {
            warehouseId,
            query,
          } as ExecuteQueryRequestDto
        }
        validationSchema={yup
          .object()
          .shape({ query: yup.string().required("Enter a query") })}
        onSubmit={formHandler(
          isOss,
          {
            id: "warehouse_query_form",
            pickFieldsForEvent: ["query"],
          },
          warehouseService.executeQuery,
          (res) => {
            getQueryResults(res.queryId);
          }
        )}
        enableReinitialize
      >
        {({ isSubmitting }) => (
          <Form>
            <InputField
              type="textarea"
              minRows={3}
              title="Query"
              name="query"
              onChange={setQuery}
              placeholder="Enter Query..."
              className="border-0 border-bottom mono-font"
            />
            <div className="d-flex align-items-center">
              <Button
                type="submit"
                className="btn mt-2"
                disabled={isSubmitting}
                variant="outline-primary"
              >
                Run Query
                <IconPlayerPlay size={14} style={{ marginRight: "5px" }} />
                {isSubmitting && <IconLoader className="spinner-icon" />}
              </Button>
              {queryResults && queryResults.status === "SUCCEEDED" && (
                <Button
                  type="button"
                  className="btn btn-primary mt-2 ms-2"
                  variant="outline-primary"
                  onClick={nextStep}
                >
                  Continue
                  <IconChevronRight size={18} />
                </Button>
              )}
            </div>
          </Form>
        )}
      </Formik>
      {queryResults && renderQueryResults(queryResults)}
    </Layout>
  );
};

function renderQueryResults(result: ExecuteQueryResultsDto) {
  if (result.status === "PENDING") {
    return (
      <div>
        <p>Query in progress...</p>
        <div className="table-responsive mx-auto mt-2">
          <Table hover>
            <tbody>
              <tr className="pt-4 pb-4">
                <td>
                  <div className="linear-background"></div>
                </td>
                <td>
                  <div className="linear-background"></div>
                </td>
                <td>
                  <div className="linear-background"></div>
                </td>
                <td>
                  <div className="linear-background"></div>
                </td>
              </tr>
            </tbody>
          </Table>
        </div>
      </div>
    );
  } else if (result.status === "FAILED") {
    return <p>Query failed with error: {result.failureMessage}</p>;
  } else if (result.queryResults) {
    return (
      <>
        <div className="table-responsive mx-auto mt-2">
          <Table hover>
            <thead>
              <tr>
                {result.queryResults.headers.map((header, i) => (
                  <th key={i}>{header}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {result.queryResults.rows.map((row, i) => (
                <tr key={i}>
                  {row.map((item, j) => (
                    <td key={j}>{item}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </>
    );
  }
}

export default WarehouseModel;
