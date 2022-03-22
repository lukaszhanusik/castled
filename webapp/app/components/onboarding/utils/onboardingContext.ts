function onboardingContext(type: string) {
  localStorage.setItem("onboarding_step", type);
}

function getOnboardingContext() {
  return localStorage.getItem("onboarding_step");
}

export { onboardingContext, getOnboardingContext };
