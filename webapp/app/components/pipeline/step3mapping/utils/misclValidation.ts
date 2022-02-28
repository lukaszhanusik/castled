import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";

export default function misclValidation(
  values: any,
  mappingGroups?: MappingGroup[]
) {
  const errors: { [s: string]: string }[] = [];

  const hasMisclFields = mappingGroups?.filter(
    (group) => group.type == "MISCELLANEOUS_FIELDS"
  );

  if (hasMisclFields) {
    // let countOptionalFields = hasMisclFields[0].optionalFields.length;
    let misclFieldsTrack = 0;

    // remove the empty values from valuesect
    const userInputObject = { ...values };
    for (let [key, value] of Object.entries(userInputObject)) {
      if (!value) {
        delete userInputObject[key];
      }
    }

    for (let [key, value] of Object.entries(userInputObject)) {
      if (key.includes("MISCELLANEOUS_FIELDS-")) {
        misclFieldsTrack += 1;
      }
    }
    // console.log(misclFieldsTrack);
    if (misclFieldsTrack) {
      const trackedAllMisclFields = [];
      for (let [key, value] of Object.entries(userInputObject)) {
        let destinationWarehouseTrack = "MISCELLANEOUS_FIELDS-warehouseField-";
        let destinationAppFieldTrack = "MISCELLANEOUS_FIELDS-appField-";

        if (key.includes(destinationWarehouseTrack)) {
          let replacedKey = key.replace(destinationWarehouseTrack, "");
          // console.log(replacedKey);
          for (let [key0, value0] of Object.entries(userInputObject)) {
            if (key0.includes(`${destinationAppFieldTrack}${replacedKey}`)) {
              trackedAllMisclFields.push(true);
            }
          }
        }
      }
      // console.log(trackedAllMisclFields.length);

      if (trackedAllMisclFields.length * 2 !== misclFieldsTrack) {
        errors.push({
          misclFieldsValidation:
            "Both sides of Miscellaneous Fields must be filled.",
        });
      }
    }
  }
  // console.log(errors);
  return errors;
}
