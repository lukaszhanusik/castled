import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import { deleteItemFromLocalStorage } from "../utils/MappingAutoFill";
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
  // This is just to provide initial rerender on auto add rows in useEffect.
  // After inital onmount returns undefined, we need to populate it on next render.
  const [trackFieldsElement, setTrackFieldsElement] = useState<JSX.Element[]>(
    []
  );

  useEffect(() => {
    if (optionalFields) {
      setOptionalFieldsElement((prev) => [...prev, ...optionalFields]);
      setTrackFieldsElement((prev) => [...prev, ...optionalFields]);
    }
  }, []);

  // On mount check if fields are there in localStorage
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("destinationFieldForm");
    if (getLocalStorageItem) {
      const destinationFieldsForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, destinationFieldsForm);

      // Loop over localStorage items to auto add rows
      for (let key of Object.keys(destinationFieldsForm)) {
        const [fieldName, status, type, index] = key.split("-");
        if (key.includes("optional-warehouseField")) {
          const elementToAdd = trackFieldsElement.filter(
            (ele) => ele.key === index
          );
          if (elementToAdd[0]) {
            setOptionalRow((prev) => [...prev, elementToAdd[0]]);
            setOptionalFieldsElement((prevState) =>
              prevState.filter((field) => field.key !== elementToAdd[0].key)
            );
          }
        }
      }
    }
  }, [trackFieldsElement]);

  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  const optionalFields = destinationFieldSection[0].optionalFields?.map(
    (optionalField, i) => (
      <DestinationFieldRows
        key={i}
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
          deleteRow(i.toString());
          deleteItemFromLocalStorage(i.toString(), "destinationFieldForm");
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
        defaultAppValue={
          defaultValue("optional-appField", i) && {
            value: defaultValue("optional-appField", i),
            label: defaultValue("optional-appField", i)
              .split("_")
              .map((word: string[]) => word[0].toUpperCase() + word.slice(1))
              .join(" "),
          }
        }
        defaultWarehouseValue={
          defaultValue("optional-warehouseField", i) && {
            value: defaultValue("optional-warehouseField", i),
            label: defaultValue("optional-warehouseField", i)
              .split("_")
              .map((word: string[]) => word[0].toUpperCase() + word.slice(1))
              .join(" "),
          }
        }
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
                    defaultWarehouseValue={
                      defaultValue("mandatory-warehouseField", i) && {
                        value: defaultValue("mandatory-warehouseField", i),
                        label: defaultValue("mandatory-warehouseField", i)
                          .split("_")
                          .map(
                            (word: string[]) =>
                              word[0].toUpperCase() + word.slice(1)
                          )
                          .join(" "),
                      }
                    }
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
