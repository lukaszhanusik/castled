import { ConnectorDetails } from "./PipelineResponseDto";
export interface ModelListDto {
  id: number;
  teamId: number;
  activeSyncsCount: number;
  warehouse: ConnectorDetails;
  modelName: string;
  modelType: string;
  modelDetails: {
    sourceQuery: string;
    type: string;
  };
  queryModelPK: {
    primaryKey: string[];
  };
}
