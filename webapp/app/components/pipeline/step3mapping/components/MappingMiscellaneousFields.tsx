import { useState, useEffect } from "react";
import { Button } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";
import { AdditionalFields } from "./Layouts/AdditionalFields";
import {
  addkeysToLocalStorage,
  defaultValue,
  deleteItemFromLocalStorage,
  formatLabel,
} from "../utils/MappingAutoFill";

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  errors,
}: MappingFieldsProps) {
  const [additionalRow, setAdditionalRow] = useState<JSX.Element[]>([]);
  // const [pkChecked, setPkChecked] = useState<{ [s: string]: boolean }>({});

  // useEffect for pre_populated_rows
  useEffect(() => {
    if (mappingGroups.autoMap) {
      const initialValues = {};
      for (let ele of options) {
        // Add rows for pre_populated_rows
        addRow(ele.value, mappingGroups.pkRequired ? true : false);

        // Add values to formik values
        let key = ele.value;
        let value = ele.value;
        const initialFieldValues = {
          [`MISCELLANEOUS_FIELDS-warehouseField-${key}`]: value,
          [`MISCELLANEOUS_FIELDS-appField-${key}`]: value,
        };
        Object.assign(initialValues, initialFieldValues);
        // if (mappingGroups.pkRequired) {
        //   let newKey = { [key]: false };
        //   setPkChecked((prev) => Object.assign(prev, newKey));
        // }
      }
      Object.assign(values, initialValues);
    } else {
      addRow("0x0x0x0x0x");
    }
  }, []);

  // On mount check if fields are there in localStorage
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("misclFieldForm");
    if (getLocalStorageItem) {
      const misclFieldsForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, misclFieldsForm);
      for (let [key, value] of Object.entries(misclFieldsForm)) {
        const [field, type, identity] = key.split("-");
        if (
          !identity.includes("0x0x0x0x0x") &&
          key.includes("warehouseField")
        ) {
          setAdditionalRow((prevState) => [
            ...prevState,
            additionalFields(identity),
          ]);
        }
      }
    }
  }, []);

  // console.log(pkChecked);

  function addRow(key?: string, pkRequired: boolean = false) {
    const randomKey = Math.random().toString(15).substring(2, 15);
    setAdditionalRow((prevState) => [
      ...prevState,
      additionalFields(key || randomKey, pkRequired),
    ]);
  }

  function deleteRow(key: string) {
    // Remove primary key when deleted rows
    handleCheckboxChange(key, false);
    // filter items based on key
    setAdditionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  function keyValueDefault(type: string, key: string): string {
    return `MISCELLANEOUS_FIELDS-${type}-${key}`;
  }

  function handleCheckboxChange(key: string, isChecked: boolean) {
    setFieldValue?.(`MISCELLANEOUS_FIELDS-primaryKey-${key}`, isChecked);
    // setPkChecked((prev) => Object.assign(prev, { [key]: isChecked }));
  }

  const additionalFields = (key: string, pkRequired: boolean = false) => (
    <AdditionalFields
      key={key}
      options={options}
      pkRequired={pkRequired}
      checkboxChange={(e) => handleCheckboxChange(key, e.target.checked)}
      name={key}
      onChange={(e) => {
        setFieldValue?.(keyValueDefault("warehouseField", key), e?.value);
        addkeysToLocalStorage({
          input: e?.value,
          formType: "misclFieldForm",
          type: "warehouseField",
          index: key,
        });
      }}
      onBlur={() =>
        setFieldTouched?.(keyValueDefault("warehouseField", key), true)
      }
      handleDelete={(e) => {
        e.preventDefault();
        deleteRow(key);
        deleteItemFromLocalStorage(key, "misclFieldForm");
        setFieldValue?.(keyValueDefault("warehouseField", key), "");
        setFieldValue?.(keyValueDefault("appField", key), "");
      }}
      inputChange={(e) => {
        setFieldValue?.(keyValueDefault("appField", key), e.target.value);
        addkeysToLocalStorage({
          input: e.target.value,
          formType: "misclFieldForm",
          type: "appField",
          index: key,
        });
      }}
      inputBlur={() =>
        setFieldTouched?.(keyValueDefault("appField", key), true)
      }
      defaultWarehouseValue={
        defaultValue({
          form: "misclFieldForm",
          type: "warehouseField",
          index: key,
        })
          ? {
              value: defaultValue({
                form: "misclFieldForm",
                type: "warehouseField",
                index: key,
              }),
              label: formatLabel(
                defaultValue({
                  form: "misclFieldForm",
                  type: "warehouseField",
                  index: key,
                })
              ),
            }
          : mappingGroups.autoMap
          ? {
              value: key,
              label: key,
            }
          : undefined
      }
      defaultAppValue={
        mappingGroups.autoMap
          ? key
          : defaultValue({
              form: "misclFieldForm",
              type: "appField",
              index: key,
            })
      }
    />
  );

  return (
    <div className="row py-2">
      {mappingGroups && (
        <>
          <WarehouseColumn
            title={mappingGroups.title}
            description={mappingGroups.description}
            pkRequired={mappingGroups.pkRequired}
          >
            {additionalRow}
            <Button
              onClick={(e) => {
                e.preventDefault();
                addRow();
              }}
              variant="outline-primary"
              className="my-2 mx-2"
            >
              Add mapping row
            </Button>
          </WarehouseColumn>
          <ErrorMessage errors={errors} include={"miscl"} />
          <hr className="solid" />
        </>
      )}
    </div>
  );
}
