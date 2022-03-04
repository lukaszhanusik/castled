import { useState, useEffect, useRef } from "react";
import { Button } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";
import PrePopulatedFields from "./Layouts/PrePopulatedFields";
import { AdditionalFields } from "./Layouts/AdditionalFields";
import {
  addkeysToLocalStorage,
  defaultValue,
  deleteItemFromLocalStorage,
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
  const [populatedRow, setPopulatedRow] = useState<JSX.Element[]>([]);

  // useEffect for pre_populated_rows
  useEffect(() => {
    if (prePopulatedRow) {
      setPopulatedRow((prev) => [...prev, ...prePopulatedRow]);
      const initialValues = {};
      for (let ele of options) {
        let key = ele.value;
        let value = ele.value;
        const initialFieldValues = {
          [`MISCELLANEOUS_FIELDS-warehouseField-${key}`]: value,
          [`MISCELLANEOUS_FIELDS-appField-${key}`]: value,
        };
        Object.assign(initialValues, initialFieldValues);
      }
      Object.assign(values, initialValues);
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

  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function addRow(e: any, key?: string) {
    e.preventDefault();
    const randomKey = Math.random().toString(15).substring(2, 15);
    setAdditionalRow((prevState) => [
      ...prevState,
      additionalFields(key || randomKey),
    ]);
  }

  const prePopulatedRow =
    miscellaneousFieldSection[0].autoMap &&
    options.map((field) => {
      return (
        <PrePopulatedFields
          key={field.value}
          options={options}
          onChange={(e) => {
            setFieldValue?.(
              keyValueDefault("warehouseField", field.value),
              e?.value
            );
          }}
          onBlur={() =>
            setFieldTouched?.(
              keyValueDefault("warehouseField", field.value),
              true
            )
          }
          handleDelete={(e) => {
            e.preventDefault();
            deletePopulatedRow(field.value);
            setFieldValue?.(keyValueDefault("warehouseField", field.value), "");
            setFieldValue?.(keyValueDefault("appField", field.value), "");
          }}
          inputChange={(e) => {
            setFieldValue?.(
              keyValueDefault("appField", field.value),
              e.target.value
            );
          }}
          inputBlur={() =>
            setFieldTouched?.(keyValueDefault("appField", field.value), true)
          }
          inputDefaultValue={field.value}
          selectDefaultValue={{ value: field.value, label: field.label }}
        />
      );
    });

  function deletePopulatedRow(key: string) {
    // filter items based on key
    setPopulatedRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  function deleteRow(key: string) {
    // filter items based on key
    setAdditionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  function keyValueDefault(type: string, key?: string): string {
    return key
      ? `MISCELLANEOUS_FIELDS-${type}-${key}`
      : `MISCELLANEOUS_FIELDS-${type}-0x0x0x0x0x`;
  }

  const additionalFields = (key: string) => (
    <AdditionalFields
      key={key}
      options={options}
      onChange={(e) => {
        setFieldValue?.(keyValueDefault("warehouseField", key), e?.value);
        addkeysToLocalStorage(
          e?.value,
          "misclFieldForm",
          "",
          "warehouseField",
          key
        );
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
        addkeysToLocalStorage(
          e.target.value,
          "misclFieldForm",
          "",
          "appField",
          key
        );
      }}
      inputBlur={() =>
        setFieldTouched?.(keyValueDefault("appField", key), true)
      }
      defaultWarehouseValue={
        defaultValue("misclFieldForm", "", "warehouseField", key) && {
          value: defaultValue("misclFieldForm", "", "warehouseField", key),
          label: defaultValue("misclFieldForm", "", "warehouseField", key)
            .split("_")
            .map((word: string[]) => word[0].toUpperCase() + word.slice(1))
            .join(" "),
        }
      }
      defaultAppValue={defaultValue("misclFieldForm", "", "appField", key)}
    />
  );

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <>
            <WarehouseColumn
              title={field.title}
              description={field.description}
            >
              {miscellaneousFieldSection[0].autoMap && populatedRow}

              <AdditionalFields
                key={"0x0x0x0x0x"}
                options={options}
                onChange={(e) => {
                  setFieldValue?.(keyValueDefault("warehouseField"), e?.value);
                  addkeysToLocalStorage(
                    e?.value,
                    "misclFieldForm",
                    "",
                    "warehouseField",
                    "0x0x0x0x0x"
                  );
                }}
                onBlur={() =>
                  setFieldTouched?.(keyValueDefault("warehouseField"), true)
                }
                handleDelete={(e) => {
                  e.preventDefault();
                  deleteRow("0x0x0x0x0x");
                  deleteItemFromLocalStorage("0x0x0x0x0x", "misclFieldForm");
                  setFieldValue?.(keyValueDefault("warehouseField"), "");
                  setFieldValue?.(keyValueDefault("appField"), "");
                }}
                inputChange={(e) => {
                  setFieldValue?.(keyValueDefault("appField"), e.target.value);
                  addkeysToLocalStorage(
                    e.target.value,
                    "misclFieldForm",
                    "",
                    "appField",
                    "0x0x0x0x0x"
                  );
                }}
                inputBlur={() =>
                  setFieldTouched?.(keyValueDefault("appField"), true)
                }
                defaultWarehouseValue={
                  defaultValue(
                    "misclFieldForm",
                    "",
                    "warehouseField",
                    "0x0x0x0x0x"
                  ) && {
                    value: defaultValue(
                      "misclFieldForm",
                      "",
                      "warehouseField",
                      "0x0x0x0x0x"
                    ),
                    label: defaultValue(
                      "misclFieldForm",
                      "",
                      "warehouseField",
                      "0x0x0x0x0x"
                    )
                      .split("_")
                      .map(
                        (word: string[]) =>
                          word[0].toUpperCase() + word.slice(1)
                      )
                      .join(" "),
                  }
                }
                defaultAppValue={defaultValue(
                  "misclFieldForm",
                  "",
                  "appField",
                  "0x0x0x0x0x"
                )}
              />
              {additionalRow}
              <Button
                onClick={addRow}
                variant="outline-primary"
                className="my-2 mx-2"
              >
                Add mapping row
              </Button>
            </WarehouseColumn>
            <ErrorMessage errors={errors} include={"miscl"} />
            <hr className="solid" />
          </>
        ))}
    </div>
  );
}
