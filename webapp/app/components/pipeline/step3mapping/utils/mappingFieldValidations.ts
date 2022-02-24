import transformMapping, { MappingReturnObject } from "./transformMapping";

export default function mappingFieldValidations(values: any) {
  const fields: MappingReturnObject = transformMapping(values);

  const { fieldMappings } = fields;
  const errors: { [s: string]: string }[] = [];

  // AppField repeating schema validation
  function appFieldRepeatingValidations() {
    for (let i = 0; i < fieldMappings.length; i++) {
      for (let j = i + 1; j < fieldMappings.length; j++) {
        if (fieldMappings[i].appField == fieldMappings[j].appField) {
          errors.push({ appFieldRepeating: "App Field Repeating ERROR" });
        }
      }
    }
  }

  function primaryKeyBothRowNeeded(obj: any) {
    let count = 0;
    for (let [key, value] of Object.entries(obj)) {
      if (key.includes("PRIMARY_KEYS")) {
        if (key.includes("appField")) {
          for (let [key0, value0] of Object.entries(obj)) {
            if (key.includes("PRIMARY_KEYS")) {
              if (key0.includes("warehouseField")) {
                count += 1;
              }
            }
          }
        }
      }
    }
    return count;
  }
  appFieldRepeatingValidations();
  if (!primaryKeyBothRowNeeded(values)) {
    errors.push({
      fillBothPrimaryFields: "Both Primary key fields must be filled",
    });
  }
  return errors;
}
