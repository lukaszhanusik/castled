import { OnboardCountDto } from "@/app/common/dtos/OnboardCountDto";
import pipelineService from "@/app/services/pipelineService";

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

function pipelineCountAPICall() {
  pipelineService.onboardCount().then(({ data }) => {
    localStorage.setItem("onboarding_count", JSON.stringify(data.pipelines));
    localStorage.setItem(
      "demo_onboarding_count",
      JSON.stringify(data.demoPipelines)
    );
  });
}

export {
  onboardingContext,
  getOnboardingContext,
  setStepsDoneCount,
  pipelineCountAPICall,
};
