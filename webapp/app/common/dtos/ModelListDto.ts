import { ConnectorDetails } from "./PipelineResponseDto";
export interface ModelListDto {
  id: number;
  teamId: number;
  activeSyncsCount: number;
  warehouse: ConnectorDetails;
  modelName: string;
  modelType: string;
  demo: boolean;
  modelDetails: {
    sourceQuery: string;
    type: string;
  };
  queryModelPK: {
    primaryKey: string[];
  };
}
