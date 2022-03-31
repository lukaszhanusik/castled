import { PipelineWizardStepProps } from "@/app/components/pipeline/PipelineWizard";
import Layout from "@/app/components/layout/Layout";
import { Form, Formik } from "formik";
import formHandler from "@/app/common/utils/formHandler";
import React, { useEffect, useState } from "react";
import warehouseService from "@/app/services/warehouseService";
import { usePipelineWizContext } from "@/app/common/context/pipelineWizardContext";
import Loading from "@/app/components/common/Loading";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { ExecuteQueryResultsDto } from "@/app/common/dtos/ExecuteQueryResultsDto";
import { PipelineSchemaResponseDto } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { Table } from "react-bootstrap";
import _ from "lodash";
import InputField from "@/app/components/forminputs/InputField";
import { Button } from "react-bootstrap";
import { useSession } from "@/app/common/context/sessionContext";
import { IconChevronRight, IconLoader, IconPlayerPlay } from "@tabler/icons";
import * as yup from "yup";
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import modelService from "@/app/services/modelService";
import Select from "react-select";
import { NextRouter, useRouter } from "next/router";

const CreateModel = ({
  curWizardStep,
  steps,
  setCurWizardStep,
  stepGroups,
  onFinish,
}: PipelineWizardStepProps) => {
  const [queryResults, setQueryResults] = useState<
    ExecuteQueryResultsDto | undefined
  >();

  const router = useRouter();
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
  const [pipelineSchema, setPipelineSchema] = useState<
    PipelineSchemaResponseDto | undefined
  >();
  const { isOss } = useSession();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [warehouseFields, setWarehouseFields] = useState<SelectOptionDto[]>();

  const updateDemoQueries = (whId: number) => {
    warehouseService.demoQueries(whId).then(({ data }) => {
      setDemoQueries(data);
    });
  };

  const createModel = () => {
    if (
      !modelName &&
      (!primaryKeys || (primaryKeys && primaryKeys.length === 0))
    ) {
      bannerNotificationService.error(
        "Please enter model name and select atleast one primary key"
      );
      return;
    }

    if (!modelName) {
      bannerNotificationService.error("Model name cannot be empty");
      return;
    }

    if (!primaryKeys || (primaryKeys && primaryKeys.length === 0)) {
      bannerNotificationService.error("Please select atleast one primary key");
      return;
    }

    modelService
      .create({
        warehouseId: pipelineWizContext.values?.warehouseId || 0, //delete this after testing
        name: modelName,
        type: "SQL",
        details: {
          type: "SQL",
          sourceQuery: query || "",
        },
        queryPK: {
          primaryKeys: primaryKeys || [],
        },
      })
      .then(({ data }) => {
        bannerNotificationService.success("Model created successfully");
        {
          onFinish && onFinish(data.warehouseId);
        }
      })
      .catch((err: any) => {
        bannerNotificationService.error(err?.response?.data?.message);
      });
  };

  const [primaryKeys, setValue] = useState<string[]>();
  const [modelName, setModelName] = useState("");
  const handleChange = (event: any) => {
    if (event && event[0]) {
      setValue(_.map(event, "value"));
    }
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

        if (
          data &&
          data.queryResults &&
          data.queryResults.headers &&
          data.queryResults.headers[0]
        ) {
          const fields = _.map(
            data.queryResults.headers,
            function (el: string) {
              return { label: el, value: el, title: el };
            }
          );

          setWarehouseFields(fields);
        }
        setQueryResults(data);
      })
      .catch(() => {
        bannerNotificationService.error("Query failed unexpectedly");
      });
  };

  return (
    <Layout
      title={steps[curWizardStep].title}
      subTitle={steps[curWizardStep].description}
      centerTitle={true}
      steps={steps}
      isFluid={true}
      stepGroups={stepGroups}
    >
      <div className="row">
        <div className="col-6">
          <Formik
            initialValues={
              {
                warehouseId,
                query,
              } as any
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
              <Form className="create-model">
                <InputField
                  type="code"
                  title="Query"
                  name="query"
                  onChange={setQuery}
                  height="345px"
                  placeholder="Enter Query..."
                  className="border-0 border-bottom mono-font"
                />
                {queryResults && queryResults.status === "SUCCEEDED" && (
                  <>
                    <label className="mt-2 form-label">
                      Model Name <span className="required-icon">*</span>
                    </label>
                    <input
                      placeholder="Model Name"
                      type="text"
                      className="form-control form-control-md"
                      onChange={(e) => {
                        setModelName(e.currentTarget.value);
                      }}
                    />

                    <label className="mt-2 form-label">
                      Primary Keys <span className="required-icon">*</span>
                    </label>
                    <Select
                      onChange={(event) => handleChange(event)}
                      options={warehouseFields!}
                      isMulti={true}
                      name="primaryKeys"
                    ></Select>
                  </>
                )}
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
                      className="btn btn-outline-primary mt-2 ms-2"
                      onClick={() => createModel()}
                    >
                      Save
                    </Button>
                  )}
                </div>
              </Form>
            )}
          </Formik>
        </div>
        <div className="col-6">
          {queryResults ? (
            renderQueryResults(queryResults)
          ) : (
            <img className="ht-400" src="/images/model.png" />
          )}
        </div>
      </div>
    </Layout>
  );
};

function renderQueryResults(result: ExecuteQueryResultsDto) {
  if (result.status === "PENDING") {
    return (
      <div>
        <p>Query in progress...</p>
        <div className="table-responsive mx-auto loading-table">
          <Table>
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
          <Table>
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

export default CreateModel;
