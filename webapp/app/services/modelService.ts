import { ModelCreateRequestDto } from '../common/dtos/ModelCreateRequestDto';
import http from "@/app/services/http";
import { AxiosResponse } from "axios";
import { ModelListDto } from "../common/dtos/ModelListDto";

export default {
    get: (modelId?: number): Promise<AxiosResponse<ModelListDto[]>> => {
        return http.get("/v1/modals", { modelId });
    },
    create: (
        req: ModelCreateRequestDto
      ): Promise<AxiosResponse<ModelCreateRequestDto>> => {
        return http.post("/v1/models", req);
    },
};