export enum AccessType {
  OAUTH = "OAUTH",
  API_KEY = "API_KEY",
  PASSWORD = "PASSWORD",
}

export const AccessTypeLabel: { [key in AccessType]: string } = {
  [AccessType.OAUTH]: "Oauth",
  [AccessType.API_KEY]: "Api Key",
  [AccessType.PASSWORD]: "Password",
};
