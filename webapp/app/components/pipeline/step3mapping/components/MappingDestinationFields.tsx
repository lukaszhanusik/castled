// Section 3 Mapping with formic

import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import DestinationFieldRows from "./Layouts/DestinationFieldRows";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingImportantFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  errors,
}: MappingFieldsProps) {
  const [optionalRow, setOptionalRow] = useState<JSX.Element[]>([]);
  const [optionalFieldsElement, setOptionalFieldsElement] = useState<
    JSX.Element[]
  >([]);
  const [form, setForm] = useState({});

  useEffect(() => {
    if (optionalFields) setOptionalFieldsElement(optionalFields);
  }, []);

  // On mount check if fields are there in localStorage
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("destinationFieldForm");
    if (getLocalStorageItem) {
      const destinationFieldsForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, destinationFieldsForm);
    }
  }, []);

  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  const optionalFields = destinationFieldSection[0].optionalFields?.map(
    (optionalField, i) => (
      <DestinationFieldRows
        key={optionalField.fieldName}
        options={options}
        destinationFieldSection={destinationFieldSection}
        isDisabled={!optionalField.optional}
        onChangeWarehouse={(e) => {
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-warehouseField-${i}`,
            e?.value
          );
          addKeysToState(e, "optional-warehouseField", i);
        }}
        onChangeAppField={(e) => {
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-appField-${i}`,
            e?.value
          );
          addKeysToState(e, "optional-appField", i);
        }}
        handleDelete={(e) => {
          e.preventDefault();
          deleteRow(optionalField.fieldName);
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-warehouseField-${i}`,
            ""
          );
          setFieldValue?.(`DESTINATION_FIELDS-optional-appField-${i}`, "");
        }}
        onBlur={() =>
          setFieldTouched?.(
            `DESTINATION_FIELDS-optional-${optionalField.fieldName}-${i}`,
            true
          )
        }
        defaultAppValue={{
          value: defaultValue("optional-appField", i),
          label: defaultValue("optional-appField", i),
        }}
        defaultWarehouseValue={{
          value: defaultValue("optional-warehouseField", i),
          label: defaultValue("optional-warehouseField", i),
        }}
        isClearable={true}
      />
    )
  );

  function addRow() {
    if (optionalFieldsElement && optionalFieldsElement.length) {
      const elementToAdd = optionalFieldsElement[0];
      if (elementToAdd) {
        // Add row to the screen
        setOptionalRow((prevState) => [...prevState, elementToAdd]);
        // Remove row from the optionalFieldsElement giving remaining row we can add
        setOptionalFieldsElement((prevState) =>
          prevState.filter((field) => field.key !== elementToAdd.key)
        );
      }
    }
  }

  function deleteRow(key: string) {
    // filter items based on key
    setOptionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );

    if (optionalFields) {
      setOptionalFieldsElement((prevState) => [
        ...prevState,
        ...optionalFields.filter((field) => field.key === key),
      ]);
    }
  }

  // AddKeys to localStorage
  function addKeysToState(e: any, type: string, index: number) {
    const form = {};
    if (destinationFieldSection.length > 0) {
      Object.assign(form, {
        [`DESTINATION_FIELDS-${type}-${index}`]: e?.value,
      });
      setForm((prev) => ({ ...prev, ...form }));
    }

    const getLocalStorageItem = localStorage.getItem("destinationFieldForm");
    const combineAllItems = getLocalStorageItem
      ? Object.assign(JSON.parse(getLocalStorageItem), form)
      : form;
    localStorage.setItem(
      "destinationFieldForm",
      JSON.stringify(combineAllItems)
    );
  }

  // Get default values from localStorage
  function defaultValue(type: string, index: number) {
    const getLocalStorageItem = localStorage.getItem("destinationFieldForm");
    if (getLocalStorageItem) {
      const primaryKeysForm = JSON.parse(getLocalStorageItem);
      return primaryKeysForm[`DESTINATION_FIELDS-${type}-${index}`];
    }
  }

  return (
    <div className="row">
      {destinationFieldSection.length > 0 &&
        destinationFieldSection.map((field) => (
          <>
            <WarehouseColumn
              title={field.title}
              description={field.description}
            >
              {field.mandatoryFields!.length > 0 &&
                field.mandatoryFields?.map((mandatoryField, i) => (
                  <DestinationFieldRows
                    key={mandatoryField.fieldName}
                    options={options}
                    defaultAppValue={{
                      value: mandatoryField.fieldName,
                      label:
                        mandatoryField.fieldDisplayName ||
                        mandatoryField.fieldName,
                    }}
                    defaultWarehouseValue={{
                      value: defaultValue("mandatory-warehouseField", i),
                      label: defaultValue("mandatory-warehouseField", i),
                    }}
                    isDisabled={!mandatoryField.optional}
                    onChangeWarehouse={(e) => {
                      setFieldValue?.(
                        `DESTINATION_FIELDS-mandatory-warehouseField-${i}`,
                        e?.value
                      );
                      setFieldValue?.(
                        `DESTINATION_FIELDS-mandatory-appField-${i}`,
                        mandatoryField.fieldName
                      );
                      addKeysToState(e, "mandatory-warehouseField", i);
                      addKeysToState(
                        { value: mandatoryField.fieldName },
                        "mandatory-appField",
                        i
                      );
                    }}
                    onBlur={() =>
                      setFieldTouched?.(
                        `DESTINATION_FIELDS-mandatory-${mandatoryField.fieldName}-${i}`,
                        true
                      )
                    }
                  />
                ))}
              {optionalRow}
              <Button
                onClick={addRow}
                variant="outline-primary"
                className="mx-2 my-2"
              >
                Add mapping row
              </Button>
            </WarehouseColumn>
            <ErrorMessage errors={errors} include={"destination"} />
            <hr className="solid" />
          </>
        ))}
    </div>
  );
}
