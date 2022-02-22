import { useState, useEffect } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";
// import AdditionalFields from "./Layouts/AdditionalFields";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  const [row, setRow] = useState<JSX.Element[]>([]);
  const [addOptionalRow, setAddOptionalRow] = useState(true);

  useEffect(() => {
    if (row.length === 0) {
      setAddOptionalRow(true);
    }
  }, [row]);
  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  function addRow() {
    if (miscellaneousFieldSection && miscellaneousFieldSection.length) {
      const randomKey = Math.floor(Math.random() * 10000000000);
      const additionalRow = (
        <AdditionalFields
          key={randomKey}
          options={options}
          mappingGroups={mappingGroups}
          addRow={addRow}
          handleDelete={(e) => {
            e.preventDefault();
            handleDelete(randomKey);
          }}
          additonalField
        />
      );
      setRow((prevState) => [...prevState, additionalRow]);
    }
  }

  function addOptional() {
    // console.log(addOptionalRow);
    if (addOptionalRow) {
      addRow();
      setAddOptionalRow(!addOptionalRow);
    }
  }

  function handleDelete(key: number) {
    setRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== String(key);
      })
    );
  }

  console.log(row);

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            <AdditionalFields
              key={`item-${row.length}`}
              options={options}
              mappingGroups={mappingGroups}
              addRow={addOptional}
              additonalField={false}
            />
            {row}
          </WarehouseColumn>
        ))}
    </div>
  );
}

interface AdditionalFieldsProps extends MappingFieldsProps {
  addRow: (e: any) => void;
  handleDelete?: (e: any) => void;
  additonalField?: boolean;
}

function AdditionalFields({
  options,
  mappingGroups,
  addRow,
  handleDelete,
  additonalField,
}: AdditionalFieldsProps) {
  return (
    <tr>
      <th className="w-50">
        <Select options={options} onChange={addRow} />
      </th>
      <th className="w-50">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
        />
      </th>
      {additonalField && <button onClick={handleDelete}>X</button>}
    </tr>
  );
}
