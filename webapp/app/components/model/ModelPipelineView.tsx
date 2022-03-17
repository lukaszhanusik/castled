import { OverlayTrigger, Tooltip, Col, Row, Table } from "react-bootstrap";
import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";
import { useRouter } from "next/router";
import { IconChevronRight } from "@tabler/icons";
import _ from "lodash";

export const ModelPipelineView = ({
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
      <Table>
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
                    <div className="px-2">
                      <span className="font-weight-bold">{field.name}</span>
                    </div>
                  </div>
                </td>
                <td className="col-3 py-2">
                  <OverlayTrigger
                    placement="right"
                    key={field.app.name}
                    overlay={<Tooltip id={field.app.name}>{_.capitalize(field.app.type)}</Tooltip>}
                  >
                    <div className="d-flex align-items-center" data-toggle="tooltip" data-placement="right" title={_.capitalize(field.app.type)}>
                      <img
                        src={field.app.logoUrl}
                        alt={field.app.name}
                        height={24}
                        className="mt-1"
                      />
                      <div className="ms-2">
                        {field.app.name}
                      </div>
                    </div>
                  </OverlayTrigger>
                </td>
                <td className="col-2 py-2">
                  {generateTime(field.jobSchedule.frequency)}
                </td>
                <td className="col-1 py-2">{field.syncStatus}</td>
                <td className="col-1 py-2 text-center">
                <IconChevronRight className="float-end me-2 text-secondary"/>
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
    </>
  );
};
