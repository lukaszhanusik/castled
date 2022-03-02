import { Col, Row, Table } from "react-bootstrap";
import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";
import { useRouter } from "next/router";

export const ModelSyncView = ({
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
      <h3 className="mb-1 mt-4 font-weight-bold">Syncs</h3>
      <p className="mb-1 mt-4">
        All the active pipelines created using this model
      </p>
      <span className="text-muted">
        Number of syncs: {activeSyncsCount && activeSyncsCount}
      </span>
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
                onClick={() => router.push(`/pipelines/${field.id}`)}
              >
                <td>{field.id}</td>
                <td>{field.name}</td>
                <td>
                  <div className="categories">
                    <Row>
                      <Col className="col-md-2">
                        <img src={field.app.logoUrl} />
                      </Col>
                      <Col className="col-md-10">
                        {field.app.name}
                        <div className="text-muted">{field.app.type}</div>
                      </Col>
                    </Row>
                  </div>
                </td>
                <td>{generateTime(field.jobSchedule.frequency)}</td>
                <td>{field.syncStatus}</td>
                <td>{">"}</td>
              </tr>
            ))}
        </tbody>
      </Table>
    </>
  );
};
