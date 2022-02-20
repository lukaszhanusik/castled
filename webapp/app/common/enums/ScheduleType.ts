export enum ScheduleType {
  CRON = "CRON",
  FREQUENCY = "FREQUENCY",
}

export enum ScheduleTimeUnit {
  MINUTES,
  HOURS,
  DAYS,
}

export const ScheduleTypeLabel: { [key in ScheduleType]: string } = {
  [ScheduleType.CRON]: "Cron",
  [ScheduleType.FREQUENCY]: "Frequency",
};

export const SchedulTimeUnitLabel: { [key in ScheduleTimeUnit]: string } = {
  [ScheduleTimeUnit.MINUTES]: "Minutes",
  [ScheduleTimeUnit.HOURS]: "Hours",
  [ScheduleTimeUnit.DAYS]: "Days",
};
