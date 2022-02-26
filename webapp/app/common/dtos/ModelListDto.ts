import { ConnectorDetails } from "./PipelineResponseDto";
export interface ModelListDto {
    id: number;
    teamId: number;
    warehouse: ConnectorDetails;
    modelName: string;
    modelDetails: {
        sourceQuery: string;
    };
    queryModelPK: {
        primaryKey: string[];
    };
}