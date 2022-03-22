function onboardingContext(type: string) {
  localStorage.setItem("onboarding_step", type);
}

export { onboardingContext };
