import React, { useEffect } from "react";
import { useSession } from "@/app/common/context/sessionContext";
import { useRouter } from "next/router";
import authService from "@/app/services/authService";
import { onboardingContext } from "@/app/components/onboarding/utils/onboardingContext";

const Logout = () => {
  const { setUser } = useSession();
  const router = useRouter();
  useEffect(() => {
    authService
      .logout()
      .then(() => {
        onboardingContext("default");
      })
      .catch(() => {})
      .finally(() => {
        router?.push("/auth/login");
        setUser(null);
      });
  });
  return <p>Redirecting...</p>;
};
export default Logout;
