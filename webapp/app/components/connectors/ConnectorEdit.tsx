import React, { useEffect, useState } from "react";
import ConnectorForm from "@/app/components/connectors/ConnectorForm";
import { ConnectorDto } from "@/app/common/dtos/ConnectorDto";
import appsService from "@/app/services/appsService";
import DefaultErrorPage from "next/error";
import { ConnectorCategory } from "@/app/common/utils/types";
import warehouseService from "@/app/services/warehouseService";
import Loading from "@/app/components/common/Loading";
import Layout from "@/app/components/layout/Layout";
import ConnectorHelpSubTitle from "./ConnectorHelpSubTitle";
import LoadingInput from "../common/LoadingInput";

interface ConnectorEditProps {
  appBaseUrl: string;
  category: ConnectorCategory;
  connectorId: number;
}

const ConnectorEdit = ({
  appBaseUrl,
  category,
  connectorId,
}: ConnectorEditProps) => {
  const [connector, setConnector] = useState<ConnectorDto | undefined | null>();

  useEffect(() => {
    const fetcher = category === "App" ? appsService : warehouseService;
    fetcher
      .getById(connectorId)
      .then(({ data }) => {
        setConnector(data);
      })
      .catch(() => {
        setConnector(null);
      });
  }, []);

  if (connector === null) return <DefaultErrorPage statusCode={404} />;
  return (
    <Layout
      title={`Edit ${category}`}
      centerTitle
      subTitle={
        <ConnectorHelpSubTitle
          description=""
          curWizardStep="configure"
          docUrl={connector?.docUrl}
        />
      }
    >
      {!connector && <LoadingInput />}
      {connector && (
        <ConnectorForm
          appBaseUrl={appBaseUrl}
          oauthCallback={category == "App" ? "/apps" : "/warehouses"}
          editConnector={connector}
          category={category}
          connectorType={connector.type}
          accessType={connector.accessType}
          onFinish={() => {}}
        />
      )}
    </Layout>
  );
};

export default ConnectorEdit;
