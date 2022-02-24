import transformMapping, { MappingReturnObject } from "./transformMapping";

export default function mappingFieldValidations(values: any) {
  const fields: MappingReturnObject = transformMapping(values);

  const { fieldMappings } = fields;
  const errors: { [s: string]: string }[] = [];

  // AppField repeating schema validation
  function appFieldRepeatingValidations(values: any) {
    for (let i = 0; i < fieldMappings.length; i++) {
      for (let j = i + 1; j < fieldMappings.length; j++) {
        if (fieldMappings[i].appField == fieldMappings[j].appField) {
          errors.push({ appFieldRepeating: "App Field Repeating ERROR" });
        }
      }
    }
  }
  appFieldRepeatingValidations(values);
  return errors;
}
