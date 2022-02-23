interface MappingReturnObject {
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
        if (key.includes("appField")) {
          primary.push(value);
        }
        if (key.includes("warehouseField")) {
          let arrObj: any = arr[Number(key.charAt(key.length - 1))] || {};
          let arrObjVal = { warehouseField: value };
          arr[Number(key.charAt(key.length - 1))] = Object.assign(
            arrObj,
            arrObjVal
          );
        }
        if (key.includes("appField")) {
          let arrObj: any = arr[Number(key.charAt(key.length - 1))] || {};
          let arrObjVal = { appField: value, skipped: false };
          arr[Number(key.charAt(key.length - 1))] = Object.assign(
            arrObj,
            arrObjVal
          );
        }
      }
    }
    fields.push(...arr);
  }

  // Destination Object Transform Function
  function destinationObjectTransform(o: { [s: string]: string }) {
    let arr = [];
    let mandatoryCount = 0;
    let optionalCount = 0;
    let allCount = 0;

    for (let [key, value] of Object.entries(o)) {
      if (key.includes("DESTINATION_FIELDS")) {
        if (key.includes("mandatory")) {
          //         let arrObj = arr[allCount] || {}
          let toReplace = `DESTINATION_FIELDS-${mandatoryCount}-mandatory-`;
          let arrObjVal = {
            warehouseField: value,
            appField: key.replace(toReplace, ""),
            skipped: false,
          };
          arr[allCount] = Object.assign({}, arrObjVal);
          mandatoryCount += 1;
          allCount += 1;
        }

        if (key.includes("optional")) {
          //         let arrObj = arr[allCount] || {}
          let toReplace = `DESTINATION_FIELDS-${optionalCount}-optional-`;
          let arrObjVal = {
            warehouseField: value,
            appField: key.replace(toReplace, ""),
            skipped: false,
          };
          arr[allCount] = Object.assign({}, arrObjVal);
          optionalCount += 1;
          allCount += 1;
        }
      }
    }
    fields.push(...arr);
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
    fields.push(...arr);
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
    fields.push(...arr.filter((e) => e && e));
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
