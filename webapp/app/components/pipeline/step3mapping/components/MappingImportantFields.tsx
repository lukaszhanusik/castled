import Select from "react-select";
import { useEffect } from "react";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import {
  addkeysToLocalStorage,
  defaultValue,
  formatLabel,
} from "../utils/MappingAutoFill";

export default function MappingImportantFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  setFieldError,
  errors,
}: MappingFieldsProps) {
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("importantParamsForm");
    if (getLocalStorageItem) {
      const importantParamsForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, importantParamsForm);
    }
  }, []);

  return (
    <div className="row py-1">
      {mappingGroups &&
        mappingGroups.fields?.map((field) => (
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
                  addkeysToLocalStorage({
                    input: e?.value,
                    formType: "importantParamsForm",
                    field: field.fieldName,
                  });
                }}
                onBlur={() =>
                  setFieldTouched?.(`IMPORTANT_PARAMS-${field.fieldName}`, true)
                }
                isClearable={field.optional}
                placeholder={"Select a column"}
                defaultValue={
                  defaultValue({
                    form: "importantParamsForm",
                    field: field.fieldName,
                  }) && {
                    value: defaultValue({
                      form: "importantParamsForm",
                      field: field.fieldName,
                    }),
                    label: formatLabel(
                      defaultValue({
                        form: "importantParamsForm",
                        field: field.fieldName,
                      })
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
