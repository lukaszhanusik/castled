import transformMapping, { MappingReturnObject } from "./transformMapping";

export default function mappingFieldValidations(values: any) {
  const fields: MappingReturnObject = transformMapping(values);

  const { fieldMappings } = fields;

  // AppField repeating schema validation
  for (let i = 0; i < fieldMappings.length; i++) {
    for (let j = i + 1; j < fieldMappings.length; j++) {
      if (fieldMappings[i].appField == fieldMappings[j].appField) {
        return "App Field Repeating ERROR";
      }
    }
  }

}
