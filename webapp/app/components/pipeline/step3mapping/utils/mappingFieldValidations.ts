import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import transformMapping, { MappingReturnObject } from "./transformMapping";

export default function mappingFieldValidations(
  values: any,
  mappingGroups?: MappingGroup[]
) {
  const fields: MappingReturnObject = transformMapping(values);

  const { fieldMappings } = fields;
  const errors: { [s: string]: string }[] = [];

  const hasPrimaryKeyObject = mappingGroups?.find(
    (group) => group.type == "PRIMARY_KEYS"
  );

  const hasDestinationFieldsObject = mappingGroups?.find(
    (group) => group.type == "DESTINATION_FIELDS"
  );

  const hasImportantParamsObject = mappingGroups?.find(
    (group) => group.type == "IMPORTANT_PARAMS"
  );

  // AppField repeating schema validation
  function appFieldRepeatingValidations() {
    for (let i = 0; i < fieldMappings.length; i++) {
      for (let j = i + 1; j < fieldMappings.length; j++) {
        if (fieldMappings[i].appField == fieldMappings[j].appField) {
          errors.push({
            appFieldRepeating:
              "Multiple warehouse columns cannot be mapped to the same app field.",
          });
        }
      }
    }
  }

  // Both row needed to be filled for primary key section 2
  function primaryKeyBothRowNeeded(obj: any) {
    let count = 0;
    if (hasPrimaryKeyObject) {
      for (let key of Object.keys(obj)) {
        if (key.includes("PRIMARY_KEYS")) {
          count++;
        }
      }
      if (count !== 2) {
        errors.push({
          fillBothPrimaryFields:
            "Both warehouse column and app field are mandatory.",
        });
      }
    }
    // let appFieldCount = 0;
    // let warehouseFieldCount = 0;
    // let hasPrimaryKey = false;
    // for (let [key, value] of Object.entries(obj)) {
    //   if (key.includes("PRIMARY_KEYS-appField")) {
    //     appFieldCount += 1;
    //     for (let [key0, value0] of Object.entries(obj)) {
    //       if (key0.includes("PRIMARY_KEYS-warehouseField")) {
    //         warehouseFieldCount += 1;
    //       }
    //     }
    //     hasPrimaryKey = true;
    //   }
    // }
    // if (hasPrimaryKey && (appFieldCount == 0 || warehouseFieldCount == 0)) {
    //   errors.push({
    //     fillBothPrimaryFields:
    //       "Both warehouse column and app field are mandatory.",
    //   });
    // }
  }

  // Primary keys mandatory validation section 2
  // function primaryKeysMandatoryValidation(obj: any) {
  //   let primaryCount = 0;
  //   let hasPrimaryKey = mappingGroups?.some(
  //     (group) => group.type == "PRIMARY_KEYS"
  //   );
  //   // console.log("hasPrimaryKey", hasPrimaryKey);
  //   if (hasPrimaryKey) {
  //     for (let [key, value] of Object.entries(obj)) {
  //       if (key.includes("PRIMARY_KEYS")) {
  //         primaryCount += 1;
  //       }
  //     }
  //   }
  //   if (hasPrimaryKey && !primaryCount) {
  //     errors.push({
  //       primaryKeyMandatory:
  //         "Both warehouse column and app field are mandatory.",
  //     });
  //   }
  // }

  // Important Params mandatory validation section 1
  function importantParamsMandatoryValidation(obj: any) {
    let importantParamsCount = 0;
    let hasImportantParams = mappingGroups?.filter(
      (group) => group.type == "IMPORTANT_PARAMS"
    )[0];

    if (hasImportantParams) {
      let importantParamsFieldsCount = hasImportantParams.fields?.reduce(
        (acc, group) => (!group.optional ? acc + 1 : acc),
        0
      );

      for (let [key, value] of Object.entries(obj)) {
        if (key.includes("IMPORTANT_PARAMS")) {
          importantParamsCount += 1;
        }
      }

      if (importantParamsFieldsCount) {
        if (importantParamsCount < importantParamsFieldsCount) {
          errors.push({
            importantParamsMandatory:
              "All questions marked mandatory (*) needs to be answered.",
          });
        }
      }
    }
  }

  // Validation for mandatory and both filled optional on section 3
  function destinationFieldsValidations(obj: any) {
    const hasDestinationFields = mappingGroups?.filter(
      (group) => group.type == "DESTINATION_FIELDS"
    );

    if (hasDestinationFields) {
      if (hasDestinationFields[0].mandatoryFields) {
        let mandatoryFieldsCount = 0;
        let countOptionalFields =
          hasDestinationFields[0].mandatoryFields.length;
        for (let [key, value] of Object.entries(obj)) {
          if (key.includes("DESTINATION_FIELDS-mandatory")) {
            mandatoryFieldsCount += 1;
          }
        }
        // Check if user has entered destination fields or not
        if (mandatoryFieldsCount !== countOptionalFields * 2) {
          errors.push({
            destinationFieldsMandatory:
              "Warehouse column is mandatory for all rows marked mandatory (*).",
          });
        }
      }
      if (hasDestinationFields[0].optionalFields) {
        // let countOptionalFields = hasDestinationFields[0].optionalFields.length;
        let optionalFieldsTrack = 0;

        const userInputObject = { ...obj };
        for (let [key, value] of Object.entries(userInputObject)) {
          if (!value) {
            delete userInputObject[key];
          }
        }
        // console.log(userInputObject);
        for (let [key, value] of Object.entries(userInputObject)) {
          if (key.includes("DESTINATION_FIELDS-optional")) {
            optionalFieldsTrack += 1;
          }
        }
        // console.log(optionalFieldsTrack);
        if (optionalFieldsTrack) {
          const trackedOptionalFields = [];
          for (let [key, value] of Object.entries(userInputObject)) {
            let destinationWarehouseTrack =
              "DESTINATION_FIELDS-optional-warehouseField-";
            let destinationAppFieldTrack =
              "DESTINATION_FIELDS-optional-appField-";

            if (key.includes(destinationWarehouseTrack)) {
              let replacedKey = Number(
                key.replace(destinationWarehouseTrack, "")
              );
              for (let [key0, value0] of Object.entries(userInputObject)) {
                if (
                  key0.includes(`${destinationAppFieldTrack}${replacedKey}`)
                ) {
                  trackedOptionalFields.push(true);
                }
              }
            }
          }

          if (trackedOptionalFields.length * 2 !== optionalFieldsTrack) {
            errors.push({
              destinationFieldsOptional:
                "Both Warehouse Column and App Fields must be filled.",
            });
          }
        }
      }
    }
  }

  appFieldRepeatingValidations();
  hasPrimaryKeyObject && primaryKeyBothRowNeeded(values);
  // hasPrimaryKeyObject && primaryKeysMandatoryValidation(values);
  hasImportantParamsObject && importantParamsMandatoryValidation(values);
  hasDestinationFieldsObject && destinationFieldsValidations(values);

  return errors;
}
