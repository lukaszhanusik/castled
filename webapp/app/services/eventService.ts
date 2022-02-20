import { StringAnyMap } from "@/app/common/utils/types";
import http from "@/app/services/http";
import { LoggedInUserDto } from "@/app/common/dtos/LoggedInUserDto";

export default {
  load: (user: LoggedInUserDto | null | undefined) => {
    let userProps = {};
    if (user) {
      userProps = {
        userId: user.id,
        userEmailId: user.email,
        userRole: user.role,
        userTeamId: user.teamId,
        userDomain: user.email.substring(user.email.indexOf("@") + 1),
        userFullName: user.name,
        userAvatar: user.avatar,
        userCreatedTs: user.createdTs,
      };
    }
  },
  send: (props: StringAnyMap) => {
    if (process.browser) {
      http.post("/v1/tracking", props).catch(() => {
        console.log(`tracking failed for event ${props.event}`);
      });
    }
  },

  trackConversion: (conversionId: string| string[]) => {
    if (process.browser) {
      http.get(`/v1/tracking/${conversionId}`).catch(() => {
        console.log(`tracking conversion failed for event ${conversionId}`);
      });
    }
  },
};
