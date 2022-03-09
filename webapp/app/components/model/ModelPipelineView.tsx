import { Table } from "react-bootstrap";
import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";
import { useRouter } from "next/router";

const ModelPipelineView = ({
  activeSyncDetails,
  activeSyncsCount,
}: Pick<ModelResponseDto, "activeSyncDetails" | "activeSyncsCount">) => {
  const router = useRouter();

  const generateTime = (seconds: number) => {
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds - hours * 3600) / 60);
    if (days > 0) return `${days} Days`;
    if (hours > 0) return `${hours} Hours`;
    if (minutes > 0) return `${minutes} Minutes`;
  };

  return (
    <>
      <div className="text-center">
        <h2 className="mb-1 mt-4 font-weight-bold">Pipelines</h2>
        <p className="mb-1 mt-2">
          All the active pipelines created using this model
        </p>
      </div>
      <div className="my-2">
        <span className="text-muted">
          Number of syncs: {activeSyncsCount && activeSyncsCount}
        </span>
      </div>
      <Table hover>
        <thead>
          <tr>
            <th></th>
            <th>Pipeline Name</th>
            <th>Destination</th>
            <th>Frequency</th>
            <th>Enabled</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {activeSyncDetails &&
            activeSyncDetails.map((field, index) => (
              <tr
                key={index}
                className="align-middle my-1"
                onClick={() => router.push(`/pipelines/${field.id}`)}
              >
                <td className="col-1 py-2">{field.id}</td>
                <td className="col-3 py-2">
                  <div className="d-flex align-items-center">
                    {!field.deleted ? (
                      <img
                        className="status-mark"
                        src="/images/check-good-mark.svg"
                      />
                    ) : (
                      <img
                        className="status-mark"
                        src="/images/cross-mark.svg"
                      />
                    )}
                    <div className="px-2">
                      <span className="font-weight-bold">{field.name}</span>
                    </div>
                  </div>
                </td>
                <td className="col-3 py-2">
                  <div className="d-flex align-items-center">
                    <img className="app-logo-url" src={field.app.logoUrl} />
                    <div className="px-2">
                      <span className="font-weight-bold">{field.app.name}</span>
                      <div className="text-muted ">{field.app.type}</div>
                    </div>
                  </div>
                </td>
                <td className="col-2 py-2">
                  {generateTime(field.jobSchedule.frequency)}
                </td>
                <td className="col-1 py-2">{field.syncStatus}</td>
                <td className="col-1 py-2 text-center">
                  <img
                    className="w-25 h-25"
                    src="/images/right-arrow-warehouse.svg"
                    alt="Right Arrow for Warehouse"
                  />
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
    </>
  );
};

export default ModelPipelineView;
