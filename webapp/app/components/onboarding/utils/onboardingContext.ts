import { OnboardCountDto } from "@/app/common/dtos/OnboardCountDto";

function onboardingContext(type: string) {
  localStorage.setItem("onboarding_step", type);
}

function getOnboardingContext() {
  return localStorage.getItem("onboarding_step");
}

function setStepsDoneCount(data: OnboardCountDto) {
  const onboardCount = Object.values(data).reduce(
    (acc: number, curr: number) => {
      if (curr) {
        return acc + 1;
      }
      return acc;
    }
  );
  localStorage.setItem("all_count_done", JSON.stringify(onboardCount));
  return onboardCount;
}

export { onboardingContext, getOnboardingContext, setStepsDoneCount };
