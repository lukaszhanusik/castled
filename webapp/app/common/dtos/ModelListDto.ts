import { ConnectorDetails } from "./PipelineResponseDto";
export interface ModelListDto {
  id: number;
  teamId: number;
  activeSyncsCount: number;
  warehouse: ConnectorDetails;
  name: string;
  type: string;
  demo: boolean;
  details: {
    sourceQuery: string;
    type: string;
  };
  queryPK: {
    primaryKey: string[];
  };
}
