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

  function addRow(btn: boolean) {
    const randomKey = Math.random().toString(15).substring(2, 15);
    setAdditionalRow((prevState) => [
      ...prevState,
      additionalFields(randomKey, btn),
    ]);
  }

  function addOptional() {
    if (addOptionalRow) {
      addRow(true);
      setAddOptionalRow(!addOptionalRow);
    }
  }

  function deleteRow(key: string) {
    // filter items based on key
    setAdditionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  const additionalFields = (key: string, button: boolean) => (
    <AdditionalFields
      key={key}
      options={options}
      onChange={(e) => {
        setFieldValue?.(`MISCELLANEOUS_FIELDS-warehouseField-${key}`, e?.value);
        addRow(true);
      }}
      onBlur={() =>
        setFieldTouched?.(`MISCELLANEOUS_FIELDS-warehouseField-${key}`, true)
      }
      handleDelete={(e) => {
        e.preventDefault();
        deleteRow(key);
        setFieldValue?.(`MISCELLANEOUS_FIELDS-warehouseField-${key}`, "");
        setFieldValue?.(`MISCELLANEOUS_FIELDS-appField-${key}`, "");
      }}
      button={button}
      inputChange={(e) => {
        setFieldValue?.(`MISCELLANEOUS_FIELDS-appField-${key}`, e.target.value);
      }}
      inputBlur={() =>
        setFieldTouched?.(`MISCELLANEOUS_FIELDS-appField-${key}`, true)
      }
    />
  );
  // console.log(additionalRow);

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            <AdditionalFields
              key={"0x0x0x0x0x0x0x"}
              options={options}
              onChange={(e) => {
                setFieldValue?.(
                  "MISCELLANEOUS_FIELDS-warehouseField-0x0x0x0x0x0x0x",
                  e?.value
                );
                addOptional();
              }}
              onBlur={() =>
                setFieldTouched?.(
                  "MISCELLANEOUS_FIELDS-warehouseField-0x0x0x0x0x0x0x",
                  true
                )
              }
              button={false}
              inputChange={(e) => {
                setFieldValue?.(
                  "MISCELLANEOUS_FIELDS-appField-0x0x0x0x0x0x0x",
                  e.target.value
                );
              }}
              inputBlur={() =>
                setFieldTouched?.(
                  "MISCELLANEOUS_FIELDS-appField-0x0x0x0x0x0x0x",
                  true
                )
              }
            />
            {additionalRow}
          </WarehouseColumn>
        ))}
    </div>
  );
}

interface AdditionalFieldsProps extends DestinationFieldRowsProps {
  button: boolean;
  inputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  inputBlur: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

function AdditionalFields({
  options,
  values,
  onChange,
  onBlur,
  handleDelete,
  button,
  inputChange,
  inputBlur,
}: AdditionalFieldsProps) {
  const [isSelected, setIsSelected] = useState(false);

  return (
    <tr>
      <th className="w-50">
        <Select options={options} onChange={onChange} onBlur={onBlur} />
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
      {button && (
        <button onClick={handleDelete} className="btn btn-danger">
          X
        </button>
      )}
    </tr>
  );
}
