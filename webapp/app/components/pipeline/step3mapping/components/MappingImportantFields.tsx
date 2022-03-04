import Select from "react-select";
import { useEffect, useState } from "react";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import { ImportantParamsField } from "@/app/common/dtos/PipelineSchemaResponseDto";

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

  function addKeysToState(e: any, field: ImportantParamsField) {
    const form = {};
    if (importantParamsSection.length > 0) {
      Object.assign(form, {
        [`IMPORTANT_PARAMS-${field.fieldName}`]: e?.value,
      });
      setForm((prev) => ({ ...prev, ...form }));
    }

    const getLocalStorageItem = localStorage.getItem("importantParamsForm");
    const combineAllItems = getLocalStorageItem
      ? Object.assign(JSON.parse(getLocalStorageItem), form)
      : form;
    localStorage.setItem(
      "importantParamsForm",
      JSON.stringify(combineAllItems)
    );
  }
  // console.log(form);

  function defaultValue(field: string) {
    const getLocalStorageItem = localStorage.getItem("importantParamsForm");
    if (getLocalStorageItem) {
      const importantParamsForm = JSON.parse(getLocalStorageItem);
      return importantParamsForm[`IMPORTANT_PARAMS-${field}`];
    }
  }

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
                  addKeysToState(e, field);
                }}
                onBlur={() =>
                  setFieldTouched?.(`IMPORTANT_PARAMS-${field.fieldName}`, true)
                }
                isClearable={field.optional}
                placeholder={"Select a column"}
                defaultValue={
                  defaultValue(field.fieldName) && {
                    value: defaultValue(field.fieldName),
                    label: defaultValue(field.fieldName),
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
