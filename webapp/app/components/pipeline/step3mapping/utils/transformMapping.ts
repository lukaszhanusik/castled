export interface MappingReturnObject {
  primaryKeys: string[];
  fieldMappings: FieldMapping[];
}

interface FieldMapping {
  warehouseField: string;
  appField: string;
  skipped: boolean;
}
export default function transformMapping(obj: any): MappingReturnObject {
  const primary: string[] = [];
  const fields: FieldMapping[] = [];

  // Primary Key Transform Function
  function primaryKeyTransform(o: { [s: string]: string }) {
    let arr = [];
    for (let [key, value] of Object.entries(o)) {
      if (key.includes("PRIMARY_KEYS")) {
        let index = Number(key.charAt(key.length - 1));
        if (key.includes("appField")) {
          primary.push(value);
        }
        if (key.includes("warehouseField")) {
          let arrObj: any = arr[index] || {};
          let arrObjVal = { warehouseField: value };
          arr[index] = Object.assign(arrObj, arrObjVal);
        }
        if (key.includes("appField")) {
          let arrObj: any = arr[index] || {};
          let arrObjVal = { appField: value, skipped: false };
          arr[index] = Object.assign(arrObj, arrObjVal);
        }
      }
    }
    fields.push(
      ...arr.filter((field) => field.warehouseField && field.appField)
    );
  }

  // Destination Object Transform Function
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
    let combinedArray = mandatoryArr.concat(optionalArr);

    fields.push(
      ...combinedArray.filter(
        (field) => field && field.warehouseField && field.appField
      )
    );
  }

  // Important params transform function
  function importantParamsTransform(o: { [s: string]: string }) {
    let arr = [];
    let count = 0;
    for (let [key, value] of Object.entries(o)) {
      if (key.includes("IMPORTANT_PARAMS")) {
        let toReplace = "IMPORTANT_PARAMS-";
        let arrObjVal = {
          warehouseField: key.replace(toReplace, ""),
          appField: value,
          skipped: false,
        };
        arr[count] = Object.assign({}, arrObjVal);
        count += 1;
      }
    }
    fields.push(
      ...arr.filter((field) => field.warehouseField && field.appField)
    );
  }

  // Miscellaneous object transform Function
  function misclTransform(o: { [s: string]: string }) {
    let arr = [];
    let count = 0;
    let memo = [];

    for (let [key, value] of Object.entries(o)) {
      if (key.includes("MISCELLANEOUS_FIELDS")) {
        if (key.includes("warehouseField")) {
          let toReplace = "MISCELLANEOUS_FIELDS-warehouseField-";
          let uuid = key.replace(toReplace, "");
          let appUUID = "appField-" + uuid;

          for (let [key0, value0] of Object.entries(o)) {
            if (key0.includes(appUUID)) {
              let arrObjVal = {
                warehouseField: value,
                appField: value0,
                skipped: false,
              };
              arr[count] = Object.assign({}, arrObjVal);
              memo.push(value);
            }
          }
          count += 1;
        }
        if (key.includes("appField")) {
          let toReplace = "MISCELLANEOUS_FIELDS-appField-";
          let uuid = key.replace(toReplace, "");
          let wareUUID = "warehouseField-" + uuid;

          for (let [key0, value0] of Object.entries(o)) {
            if (key0.includes(wareUUID) && !memo.includes(value0)) {
              let arrObjVal = {
                warehouseField: value0,
                appField: value,
                skipped: false,
              };
              arr[count] = Object.assign({}, arrObjVal);
            }
          }
          count += 1;
        }
      }
    }
    fields.push(...arr.filter((e) => e.warehouseField && e.appField));
  }

  primaryKeyTransform(obj);
  destinationObjectTransform(obj);
  importantParamsTransform(obj);
  misclTransform(obj);

  return {
    fieldMappings: fields,
    primaryKeys: primary,
  };
}
