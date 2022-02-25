export const obj = {
  "DESTINATION_FIELDS-mandatory-warehouseField-0": "id",
  "DESTINATION_FIELDS-mandatory-appField-0": "email",
  "DESTINATION_FIELDS-optional-warehouseField-0": "email",
  "DESTINATION_FIELDS-optional-warehouseField-1": "company",
  "DESTINATION_FIELDS-optional-appField-0": "last_name",
  "DESTINATION_FIELDS-optional-appField-1": "first_name",
};

function destinationObjectTransform(o: any) {
  let mandatoryArr = [];
  let optionalArr = [];

  for (let [key, value] of Object.entries(o)) {
    if (key.includes("DESTINATION_FIELDS")) {
      if (key.includes("mandatory-warehouseField")) {
        let mandatoryIndex = Number(
          key.replace("DESTINATION_FIELDS-mandatory-warehouseField-", "")
        );
        for (let [key0, value0] of Object.entries(o)) {
          if (key.includes("DESTINATION_FIELDS")) {
            let mandatoryAppIndex = `mandatory-appField-${mandatoryIndex}`;
            if (key0.includes(mandatoryAppIndex)) {
              let arrObj: any = mandatoryArr[mandatoryIndex] || {};
              let arrObjVal = {
                warehouseField: value,
                appField: value0,
                skipped: false,
              };
              mandatoryArr[mandatoryIndex] = Object.assign(arrObj, arrObjVal);
            }
          }
        }
      }
    }
  }
  for (let [key, value] of Object.entries(o)) {
    if (key.includes("DESTINATION_FIELDS")) {
      if (key.includes("optional-warehouseField")) {
        let optionalIndex = Number(
          key.replace("DESTINATION_FIELDS-optional-warehouseField-", "")
        );
        for (let [key0, value0] of Object.entries(o)) {
          if (key.includes("DESTINATION_FIELDS")) {
            let optionalAppIndex = `optional-appField-${optionalIndex}`;
            if (key0.includes(optionalAppIndex)) {
              let arrObj: any = optionalArr[optionalIndex] || {};
              let arrObjVal = {
                warehouseField: value,
                appField: value0,
                skipped: false,
              };
              optionalArr[optionalIndex] = Object.assign(arrObj, arrObjVal);
            }
          }
        }
      }
    }
  }
  fields.push(...arr.filter((field) => field.warehouseField && field.appField));
}
