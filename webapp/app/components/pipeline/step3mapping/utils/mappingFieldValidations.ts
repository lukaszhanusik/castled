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

  // console.log(fieldMappings);
  // AppField repeating schema validation
  function appFieldRepeatingValidations() {
    for (let i = 0; i < fieldMappings.length; i++) {
      for (let j = i + 1; j < fieldMappings.length; j++) {
        if (fieldMappings[i].appField == fieldMappings[j].appField) {
          errors.push({
            appFieldRepeating:
              "Multiple mappings found for a destination field! A destination field should be mapped only once.",
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
  }

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

    // console.log("hasDestinationFields", hasDestinationFields);
    if (hasDestinationFields) {
      let totalMandatoryFields = 0;
      let totalOptionalFields = 0;

      // Count mandatory and optional fields
      for (let field of hasDestinationFields) {
        if (field.mandatoryFields) {
          totalMandatoryFields += field.mandatoryFields.length;
        }
        if (field.optionalFields) {
          totalOptionalFields += field.optionalFields.length;
        }
      }

      // Sanitize the empty values
      const userInputObject = { ...obj };
      for (let [key, value] of Object.entries(userInputObject)) {
        if (!value) {
          delete userInputObject[key];
        }
      }
      // console.log(obj);
      let destinationWarehouseTrack =
        "DESTINATION_FIELDS-mandatory-warehouseField-";
      let destinationAppFieldTrack = "DESTINATION_FIELDS-mandatory-appField-";

      if (!!totalMandatoryFields) {
        let actualMandatoryFields = 0;
        for (let [key, value] of Object.entries(userInputObject)) {
          if (key.includes(destinationWarehouseTrack)) {
            let replacedKey = key.replace(destinationWarehouseTrack, "");

            for (let [key0, value0] of Object.entries(userInputObject)) {
              if (key0 === `${destinationAppFieldTrack}${replacedKey}`) {
                actualMandatoryFields++;
              }
            }
          }
        }
        // Check if user has entered both mandatory destination fields or not
        if (totalMandatoryFields !== actualMandatoryFields) {
          errors.push({
            destinationFieldsMandatory:
              "Warehouse column is mandatory for all rows marked mandatory (*).",
          });
        }
      }
      if (!!totalOptionalFields) {
        // let countOptionalFields = hasDestinationFields[0].optionalFields.length;
        let optionalFieldsTrack = 0;

        // console.log(userInputObject);
        for (let [key, value] of Object.entries(userInputObject)) {
          if (key.includes("DESTINATION_FIELDS-optional")) {
            optionalFieldsTrack += 1;
          }
        }
        // console.log(userInputObject);
        if (optionalFieldsTrack) {
          let trackedOptionalFields = 0;
          let destinationOptionalWarehouseTrack =
            "DESTINATION_FIELDS-optional-warehouseField-";
          let destinationOptionalAppFieldTrack =
            "DESTINATION_FIELDS-optional-appField-";
          for (let [key, value] of Object.entries(userInputObject)) {
            if (key.includes(destinationOptionalWarehouseTrack)) {
              let replacedKey = key.replace(
                destinationOptionalWarehouseTrack,
                ""
              );

              for (let [key0, value0] of Object.entries(userInputObject)) {
                if (
                  key0 === `${destinationOptionalAppFieldTrack}${replacedKey}`
                ) {
                  trackedOptionalFields++;
                }
              }
            }
          }

          // console.log("trackedOptionalFields", trackedOptionalFields);
          // console.log(optionalFieldsTrack);
          if (trackedOptionalFields * 2 !== optionalFieldsTrack) {
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
  hasImportantParamsObject && importantParamsMandatoryValidation(values);
  hasDestinationFieldsObject && destinationFieldsValidations(values);

  return errors;
}
