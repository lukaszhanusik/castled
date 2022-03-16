import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import {
  addkeysToLocalStorage,
  defaultValue,
  deleteItemFromLocalStorage,
  formatLabel,
} from "../utils/MappingAutoFill";
import DestinationFieldRows from "./Layouts/DestinationFieldRows";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingDestinationFields({
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

  const optionalFields = mappingGroups.optionalFields?.map((optionalField) => {
    return (
      <DestinationFieldRows
        key={optionalField.fieldName}
        options={options}
        destinationFieldSection={mappingGroups}
        isDisabled={!optionalField.optional}
        onChangeWarehouse={(e) => {
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-warehouseField-${optionalField.fieldName}`,
            e?.value
          );
          addkeysToLocalStorage({
            input: e?.value,
            formType: "destinationFieldForm",
            type: "optional-warehouseField",
            index: optionalField.fieldName,
          });
        }}
        onChangeAppField={(e) => {
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-appField-${optionalField.fieldName}`,
            e?.value
          );
          addkeysToLocalStorage({
            input: e?.value,
            formType: "destinationFieldForm",
            type: "optional-appField",
            index: optionalField.fieldName,
          });
        }}
        handleDelete={(e) => {
          e.preventDefault();
          deleteRow(optionalField.fieldName);
          deleteItemFromLocalStorage(
            optionalField.fieldName,
            "destinationFieldForm"
          );
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-warehouseField-${optionalField.fieldName}`,
            ""
          );
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-appField-${optionalField.fieldName}`,
            ""
          );
        }}
        onBlur={() =>
          setFieldTouched?.(
            `DESTINATION_FIELDS-optional-${optionalField.fieldName}-${optionalField.fieldName}`,
            true
          )
        }
        defaultAppValue={
          defaultValue({
            form: "destinationFieldForm",
            type: "optional-appField",
            index: optionalField.fieldName,
          }) && {
            value: defaultValue({
              form: "destinationFieldForm",
              type: "optional-appField",
              index: optionalField.fieldName,
            }),
            label: formatLabel(
              defaultValue({
                form: "destinationFieldForm",
                type: "optional-appField",
                index: optionalField.fieldName,
              })
            ),
          }
        }
        defaultWarehouseValue={
          defaultValue({
            form: "destinationFieldForm",
            type: "optional-warehouseField",
            index: optionalField.fieldName,
          }) && {
            value: defaultValue({
              form: "destinationFieldForm",
              type: "optional-warehouseField",
              index: optionalField.fieldName,
            }),
            label: formatLabel(
              defaultValue({
                form: "destinationFieldForm",
                type: "optional-warehouseField",
                index: optionalField.fieldName,
              })
            ),
          }
        }
        isClearable={true}
      />
    );
  });

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

  return (
    <div className="row">
      {mappingGroups && (
        <>
          <WarehouseColumn
            title={mappingGroups.title}
            description={mappingGroups.description}
          >
            {mappingGroups &&
              mappingGroups.mandatoryFields &&
              mappingGroups.mandatoryFields.map((mandatoryField, i) => (
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
                    defaultValue({
                      form: "destinationFieldForm",
                      type: "mandatory-warehouseField",
                      index: mandatoryField.fieldName,
                    }) && {
                      value: defaultValue({
                        form: "destinationFieldForm",
                        type: "mandatory-warehouseField",
                        index: mandatoryField.fieldName,
                      }),
                      label: formatLabel(
                        defaultValue({
                          form: "destinationFieldForm",
                          type: "mandatory-warehouseField",
                          index: mandatoryField.fieldName,
                        })
                      ),
                    }
                  }
                  isDisabled={!mandatoryField.optional}
                  onChangeWarehouse={(e) => {
                    setFieldValue?.(
                      `DESTINATION_FIELDS-mandatory-warehouseField-${mandatoryField.fieldName}`,
                      e?.value
                    );
                    setFieldValue?.(
                      `DESTINATION_FIELDS-mandatory-appField-${mandatoryField.fieldName}`,
                      mandatoryField.fieldName
                    );
                    addkeysToLocalStorage({
                      input: e?.value,
                      formType: "destinationFieldForm",
                      type: "mandatory-warehouseField",
                      index: mandatoryField.fieldName,
                    });
                    addkeysToLocalStorage({
                      input: mandatoryField.fieldName,
                      formType: "destinationFieldForm",
                      type: "mandatory-appField",
                      index: mandatoryField.fieldName,
                    });
                  }}
                  onBlur={() =>
                    setFieldTouched?.(
                      `DESTINATION_FIELDS-mandatory-${mandatoryField.fieldName}-${mandatoryField.fieldName}`,
                      true
                    )
                  }
                />
              ))}
            {optionalRow}
            <Button
              onClick={addRow}
              variant="outline-primary"
              className="my-2"
            >
              Add mapping row
            </Button>
          </WarehouseColumn>
          <ErrorMessage errors={errors} include={"destination"} />
          <hr className="solid" />
        </>
      )}
    </div>
  );
}
