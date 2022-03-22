import httpUtils from "@/app/common/utils/httpUtils";
import axios from "axios";
import http from "@/app/services/http";
import { LoginRequestDto } from "@/app/common/dtos/LoginRequestDto";
import { RegisterUserDTO } from "@/app/common/dtos/RegisterUserDTO";
import { AxiosResponse } from "axios";
import { LoggedInUserDto } from "@/app/common/dtos/LoggedInUserDto";
import { UserRegistrationResponse } from "../common/dtos/UserRegistrationResponse";
import { ActivateUserDTO } from "../common/dtos/ActivateUserDTO";
import { SignUpRequestDto } from "@/app/common/dtos/SignUpRequestDto";
import _ from "lodash";

export default {
  login: (request: LoginRequestDto): Promise<AxiosResponse<void>> => {
    return http.post("/v1/users/custom-signin", request);
  },
  register: (
    request: RegisterUserDTO
  ): Promise<AxiosResponse<UserRegistrationResponse>> => {
    return http.post("/v1/users/redirect", request);
  },
  registerV2: (
    request: RegisterUserDTO
  ): Promise<AxiosResponse<UserRegistrationResponse>> => {
    return http.post("/v1/users/register", request);
  },
  activateUser: (request: ActivateUserDTO): Promise<AxiosResponse<any>> => {
    return http.post("/v1/users/activate-user", request);
  },
  logout: (): Promise<AxiosResponse<void>> => {
    return http.post("/v1/users/logout");
  },
  whoAmI: (): Promise<AxiosResponse<LoggedInUserDto>> => {
    return http.get("/v1/users/whoami");
  },
  signUp: async (request: SignUpRequestDto) => {
    await axios.post(
      "/v1/users/signup",
      httpUtils.param(request as any, true),
      {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      }
    );
  },
  registerEmail: (request: {
    email: string;
  }): Promise<AxiosResponse<UserRegistrationResponse>> => {
    return http.post("/v1/users/register", request);
  },
};
