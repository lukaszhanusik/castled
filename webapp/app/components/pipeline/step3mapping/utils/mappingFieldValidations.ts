import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import transformMapping, { MappingReturnObject } from "./transformMapping";

export default function mappingFieldValidations(
  values: any,
  mappingGroups?: MappingGroup[]
) {
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

  // Both row needed to be filled for primary key section 2
  function primaryKeyBothRowNeeded(obj: any) {
    let appFieldCount = 0;
    let warehouseFieldCount = 0;
    let hasPrimaryKey = false;
    for (let [key, value] of Object.entries(obj)) {
      if (key.includes("PRIMARY_KEYS")) {
        if (key.includes("appField")) {
          for (let [key0, value0] of Object.entries(obj)) {
            if (key.includes("PRIMARY_KEYS")) {
              if (key0.includes("warehouseField")) {
                warehouseFieldCount += 1;
              }
            }
          }
          appFieldCount += 1;
        }
        hasPrimaryKey = true;
      }
    }
    if (hasPrimaryKey && (appFieldCount == 0 || warehouseFieldCount == 0)) {
      errors.push({
        fillBothPrimaryFields: "Both Primary key fields must be filled",
      });
    }
  }

  appFieldRepeatingValidations();
  primaryKeyBothRowNeeded(values);

  return errors;
}
