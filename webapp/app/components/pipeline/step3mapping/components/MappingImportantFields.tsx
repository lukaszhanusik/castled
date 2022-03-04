import Select from "react-select";
import { useEffect, useState } from "react";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import { addkeysToLocalStorage, defaultValue } from "../utils/MappingAutoFill";

export default function MappingImportantFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  setFieldError,
  errors,
}: MappingFieldsProps) {
  const [form, setForm] = useState({});

  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("importantParamsForm");
    if (getLocalStorageItem) {
      const importantParamsForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, importantParamsForm);
    }
  }, []);
  // SECTION - 1 - Mandatory fields filter from warehouseSchema
  const importantParamsSection = mappingGroups.filter((fields) => {
    return fields.type === "IMPORTANT_PARAMS" && fields.fields;
  });

  return (
    <div className="row py-1">
      {importantParamsSection.length > 0 &&
        importantParamsSection[0].fields?.map((field) => (
          <div className="flex-column align-self-center my-2">
            <div className="flex-column mx-1 my-2">
              <div className="column">
                <label className="font-weight-bold mx-1">{field.title}</label>
                {!field.optional && (
                  <label className="required-icon"> * </label>
                )}
              </div>
              <div className="row description text-muted px-3">
                {field.description}
              </div>
            </div>
            <div className="w-50 px-2">
              <Select
                key={field.fieldName}
                options={options}
                onChange={(e) => {
                  setFieldValue?.(
                    `IMPORTANT_PARAMS-${field.fieldName}`,
                    e?.value
                  );
                  addkeysToLocalStorage(
                    e?.value,
                    "importantParamsForm",
                    field.fieldName,
                    "",
                    ""
                  );
                }}
                onBlur={() =>
                  setFieldTouched?.(`IMPORTANT_PARAMS-${field.fieldName}`, true)
                }
                isClearable={field.optional}
                placeholder={"Select a column"}
                defaultValue={
                  defaultValue(
                    "importantParamsForm",
                    field.fieldName,
                    "",
                    ""
                  ) && {
                    value: defaultValue(
                      "importantParamsForm",
                      field.fieldName,
                      "",
                      ""
                    ),
                    label: defaultValue(
                      "importantParamsForm",
                      field.fieldName,
                      "",
                      ""
                    ),
                  }
                }
              />
            </div>
          </div>
        ))}
      <ErrorMessage errors={errors} include={"important"} />
      <hr className="solid" />
    </div>
  );
}
