export enum HttpMethod {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  DELETE = "DELETE",
}

export const HttpMethodLabel: { [key in HttpMethod]: string } = {
  [HttpMethod.GET]: "GET",
  [HttpMethod.POST]: "POST",
  [HttpMethod.PUT]: "PUT",
  [HttpMethod.DELETE]: "DELETE",
};
