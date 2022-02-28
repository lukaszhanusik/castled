import { useEffect, useState } from "react";
import Select from "react-select";
import {
  DestinationFieldRowsProps,
  MappingFieldsProps,
} from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
}: MappingFieldsProps) {
  const [additionalRow, setAdditionalRow] = useState<JSX.Element[]>([]);
  const [addOptionalRow, setAddOptionalRow] = useState(true);

  // useEffect(() => {
  //   addRow(false);
  // }, []);
  useEffect(() => {
    if (!additionalRow.length) {
      setAddOptionalRow(true);
    } else {
      setAddOptionalRow(false);
    }
  }, [additionalRow]);

  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function addRow() {
    const randomKey = Math.random().toString(15).substring(2, 15);
    setAdditionalRow((prevState) => [
      ...prevState,
      additionalFields(randomKey),
    ]);
  }

  // function addOptional() {
  //   if (addOptionalRow) {
  //     addRow();
  //     setAddOptionalRow(!addOptionalRow);
  //   }
  // }

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
  // console.log(additionalRow);

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {additionalRow}
            <button onClick={() => addRow()} className="btn btn-primary">
              Add row
            </button>
          </WarehouseColumn>
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
  const [isSelected, setIsSelected] = useState(false);

  return (
    <tr>
      <th className="w-50">
        <Select
          options={options}
          onChange={onChange}
          onBlur={onBlur}
          isClearable
        />
      </th>
      <th className="w-50">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
          onChange={inputChange}
          onBlur={inputBlur}
        />
      </th>

      <button onClick={handleDelete} className="btn btn-danger">
        X
      </button>
    </tr>
  );
}
