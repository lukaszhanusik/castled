export enum Role {
	ADMIN = "ADMIN",
	USER = "USER",
}

export const RoleLabel: { [key in Role]: string } = {
  [Role.ADMIN]: "Admin",
  [Role.USER]: "User",
};
