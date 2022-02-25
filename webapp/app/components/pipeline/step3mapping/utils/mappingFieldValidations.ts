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

  // Primary keys mandatory validation section 2
  function primaryKeysMandatoryValidation(obj: any) {
    let primaryCount = 0;
    let hasPrimaryKey = mappingGroups?.some(
      (group) => group.type == "PRIMARY_KEYS"
    );
    if (hasPrimaryKey) {
      for (let [key, value] of Object.entries(obj)) {
        if (key.includes("PRIMARY_KEYS")) {
          primaryCount += 1;
        }
      }
    }
    if (hasPrimaryKey && !primaryCount) {
      errors.push({
        primaryKeyMandatory: "Primary Key App Field is mandatory",
      });
    }
  }

  // Important Params mandatory validation section 1
  function importantParamsMandatoryValidation(obj: any) {
    let importantParamsCount = 0;
    let hasImportantParams = mappingGroups?.filter(
      (group) => group.type == "IMPORTANT_PARAMS"
    )[0];

    let importantParamsFieldsCount = hasImportantParams?.fields?.reduce(
      (acc, group) => acc + 1,
      0
    );
    if (hasImportantParams) {
      for (let [key, value] of Object.entries(obj)) {
        if (key.includes("IMPORTANT_PARAMS")) {
          importantParamsCount += 1;
        }
      }
    }
    if (
      hasImportantParams &&
      !(importantParamsCount == importantParamsFieldsCount)
    ) {
      errors.push({
        importantParamsMandatory: "All Important Params App Field is mandatory",
      });
    }
  }

  appFieldRepeatingValidations();
  primaryKeyBothRowNeeded(values);
  primaryKeysMandatoryValidation(values);
  importantParamsMandatoryValidation(values);

  return errors;
}
