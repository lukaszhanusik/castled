interface ConnectorHelpSubTitleProps {
  description: string;
  curWizardStep: string;
  docUrl: string | undefined;
}

const ConnectorHelpSubTitle = ({
  description,
  curWizardStep,
  docUrl,
}: ConnectorHelpSubTitleProps): JSX.Element => {
  if (curWizardStep !== "configure") return <>{description}</>;
  if (!docUrl)
    return <>Contact support if you face any issues with the setup</>;
  return (
    <p>
      Need help?{" "}
      <a href={docUrl} target="_blank">
        Read the documentation
      </a>
    </p>
  );
};

export default ConnectorHelpSubTitle;
