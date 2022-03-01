// Section 3 Mapping with formic

import { IconTrash } from "@tabler/icons";
import { useEffect, useState } from "react";
import { Placeholder } from "react-bootstrap";
import Select from "react-select";
import {
  DestinationFieldRowsProps,
  MappingFieldsProps,
} from "../types/componentTypes";
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

  useEffect(() => {
    if (optionalFields) setOptionalFieldsElement(optionalFields);
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
        }}
        onChangeAppField={(e) => {
          setFieldValue?.(
            `DESTINATION_FIELDS-optional-appField-${i}`,
            e?.value
          );
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
        defaultValue={undefined}
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

  return (
    <div className="row py-2">
      {destinationFieldSection.length > 0 &&
        destinationFieldSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.mandatoryFields!.length > 0 &&
              field.mandatoryFields?.map((mandatoryField, i) => (
                <DestinationFieldRows
                  key={mandatoryField.fieldName}
                  options={options}
                  defaultValue={{
                    value: mandatoryField.fieldName,
                    label:
                      mandatoryField.fieldDisplayName ||
                      mandatoryField.fieldName,
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
            <button type="button" onClick={addRow} className="btn btn-primary mx-2">
              Add Row
            </button>
            <ErrorMessage errors={errors} include={"destination"} />
          </WarehouseColumn>
        ))}
    </div>
  );
}

function DestinationFieldRows({
  options,
  destinationFieldSection,
  defaultValue,
  isDisabled,
  onChangeWarehouse,
  onChangeAppField,
  onBlur,
  handleDelete,
  values,
  isClearable,
}: DestinationFieldRowsProps) {
  return (
    <tr>
      <th className="col-6">
        <Select
          options={options}
          onChange={onChangeWarehouse}
          onBlur={onBlur}
          isClearable={isClearable}
          placeholder={"Select a column"}
        />
      </th>
      <th className="col-6">
        <Select
          options={
            destinationFieldSection &&
            destinationFieldSection[0].optionalFields?.map((items: any) => {
              return {
                value: items.fieldName,
                label: items.fieldDisplayName || items.fieldName,
              };
            })
          }
          defaultValue={defaultValue}
          isDisabled={isDisabled}
          onBlur={onBlur}
          isClearable={isClearable}
          onChange={onChangeAppField}
          placeholder={"Select a field"}
        />
      </th>
      <th className="col-2">
        {isDisabled && (
          <Placeholder as="td">
            <span className="required-icon">*</span>
          </Placeholder>
        )}
        {!isDisabled && (
          <Placeholder as="td">
            <IconTrash onClick={handleDelete} className="delete-btn"/>
          </Placeholder>
        )}
      </th>
    </tr>
  );
}
