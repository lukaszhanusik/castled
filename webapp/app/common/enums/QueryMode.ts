export enum QueryMode {
    INCREMENTAL ='INCREMENTAL',
    FULL_LOAD = 'FULL_LOAD'
};

export const QueryModeLabel: { [key in QueryMode]: string } = {
  [QueryMode.INCREMENTAL]: "Incremental",
  [QueryMode.FULL_LOAD]: "Full Load",
};