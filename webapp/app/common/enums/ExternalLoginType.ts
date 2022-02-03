export enum ExternalLoginType {
  GOOGLE = "GOOGLE",
}

export const ExternalLoginTypeLabel: { [key in ExternalLoginType]: string } = {
  [ExternalLoginType.GOOGLE]: "Google",
};
