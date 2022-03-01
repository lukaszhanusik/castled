import { ModelCreateRequestDto } from "../common/dtos/ModelCreateRequestDto";
import http from "@/app/services/http";
import { AxiosResponse } from "axios";
import { ModelListDto } from "../common/dtos/ModelListDto";

export default {
  get: (warehouseId?: number): Promise<AxiosResponse<ModelListDto[]>> => {
    if (warehouseId) {
      return http.get("/v1/models?warehouseId=" + warehouseId); //warehouseId=wId;
    } else {
      return http.get("/v1/models");
    }
  },
  create: (
    req: ModelCreateRequestDto
  ): Promise<AxiosResponse<ModelCreateRequestDto>> => {
    return http.post("/v1/models", req);
  },
};
