import InputSelect from "@/app/components/forminputs/InputSelect";
import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";

export default function MappingImportantFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  setFieldError,
  errors,
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
              <div className="row">
                <label className="font-weight-bold">
                  {!field.optional && <span className="required-icon">*</span>}
                  {field.title}
                </label>
              </div>
              <div className="row description text-muted">
                {field.description}
              </div>
            </div>
            <div className="w-50">
              <Select
                options={options}
                onChange={(e) =>
                  setFieldValue?.(
                    `IMPORTANT_PARAMS-${field.fieldName}`,
                    e?.value
                  )
                }
                onBlur={() =>
                  setFieldTouched?.(`IMPORTANT_PARAMS-${field.fieldName}`, true)
                }
                isClearable={field.optional}
              />
            </div>
          </div>
        ))}
      <ErrorMessage errors={errors} include={"important"} />
    </div>
  );
}
