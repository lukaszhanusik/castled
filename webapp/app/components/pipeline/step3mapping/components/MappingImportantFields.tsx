import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";

export default function MappingImportantFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
}: MappingFieldsProps) {
  // SECTION - 1 - Mandatory fields filter from warehouseSchema
  const importantParamsSection = mappingGroups.filter((fields) => {
    return fields.type === "IMPORTANT_PARAMS" && fields.fields;
  });

  return (
    <div className="row py-2">
      {importantParamsSection.length > 0 &&
        importantParamsSection[0].fields?.map((field) => (
          <div className="flex-column align-self-center my-2">
            <div className="flex-column mx-4 my-2">
              <div className="row font-weight-bold">{field.title}</div>
              <div className="row description text-muted">
                {field.title === field.description ? "" : field.description}
              </div>
            </div>
            <div className="w-50">
              <Select
                options={options}
                onChange={(e) => setFieldValue?.(field.fieldName, e)}
                onBlur={() => setFieldTouched?.(field.fieldName, true)}
              />
            </div>
          </div>
        ))}
    </div>
  );
}
