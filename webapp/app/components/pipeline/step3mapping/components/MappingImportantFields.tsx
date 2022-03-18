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
    <div className="row pb-2">
      {mappingGroups &&
        mappingGroups.fields?.map((field) => (
          <div className="flex-column align-self-center my-3">
            <div className="flex-column my-2">
              <label>{field.title}</label>
              {!field.optional && <span className="required-icon"> * </span>}

              <div className="description text-muted">{field.description}</div>
            </div>
            <div className="w-50">
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
