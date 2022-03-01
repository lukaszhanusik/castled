import { IconTrash } from "@tabler/icons";
import { useEffect, useState } from "react";
import { Button, Placeholder } from "react-bootstrap";
import Select from "react-select";
import {
  DestinationFieldRowsProps,
  MappingFieldsProps,
} from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  errors,
}: MappingFieldsProps) {
  const [additionalRow, setAdditionalRow] = useState<JSX.Element[]>([]);

  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function addRow(e: any) {
    e.preventDefault();
    const randomKey = Math.random().toString(15).substring(2, 15);
    setAdditionalRow((prevState) => [
      ...prevState,
      additionalFields(randomKey),
    ]);
  }

  function deleteRow(key: string) {
    // filter items based on key
    setAdditionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  function keyValueDefault(s: string, key?: string): string {
    return key
      ? `MISCELLANEOUS_FIELDS-${s}-${key}`
      : `MISCELLANEOUS_FIELDS-${s}-0x0x0x0x0x0x0x`;
  }

  const additionalFields = (key: string) => (
    <AdditionalFields
      key={key}
      options={options}
      onChange={(e) => {
        setFieldValue?.(keyValueDefault("warehouseField", key), e?.value);
        // addRow(true);
      }}
      onBlur={() =>
        setFieldTouched?.(keyValueDefault("warehouseField", key), true)
      }
      handleDelete={(e) => {
        e.preventDefault();
        deleteRow(key);
        setFieldValue?.(keyValueDefault("warehouseField", key), "");
        setFieldValue?.(keyValueDefault("appField", key), "");
      }}
      inputChange={(e) => {
        setFieldValue?.(keyValueDefault("appField", key), e.target.value);
      }}
      inputBlur={() =>
        setFieldTouched?.(keyValueDefault("appField", key), true)
      }
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
              <AdditionalFields
                key={"0x0x0x0x0x0x0x"}
                options={options}
                onChange={(e) => {
                  setFieldValue?.(keyValueDefault("warehouseField"), e?.value);
                  // addRow(true);
                }}
                onBlur={() =>
                  setFieldTouched?.(keyValueDefault("warehouseField"), true)
                }
                handleDelete={(e) => {
                  e.preventDefault();
                  deleteRow("0x0x0x0x0x0x0x");
                  setFieldValue?.(keyValueDefault("warehouseField"), "");
                  setFieldValue?.(keyValueDefault("appField"), "");
                }}
                inputChange={(e) => {
                  setFieldValue?.(keyValueDefault("appField"), e.target.value);
                }}
                inputBlur={() =>
                  setFieldTouched?.(keyValueDefault("appField"), true)
                }
              />
              {additionalRow}
              <Button
                onClick={addRow}
                variant="outline-primary"
                className="my-2 mx-2"
              >
                Add row
              </Button>
            </WarehouseColumn>
            <ErrorMessage errors={errors} include={"miscl"} />
            <hr className="solid" />
          </>
        ))}
    </div>
  );
}

interface AdditionalFieldsProps extends DestinationFieldRowsProps {
  inputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  inputBlur: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function AdditionalFields({
  options,
  values,
  onChange,
  onBlur,
  handleDelete,
  inputChange,
  inputBlur,
}: AdditionalFieldsProps) {
  return (
    <tr>
      <th className="col-6">
        <Select
          options={options}
          onChange={onChange}
          onBlur={onBlur}
          isClearable
          placeholder={"Select a column"}
        />
      </th>
      <th className="col-6">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
          onChange={inputChange}
          onBlur={inputBlur}
        />
      </th>
      <Placeholder as="td">
        <IconTrash onClick={handleDelete} className="delete-btn" />
      </Placeholder>
    </tr>
  );
}
