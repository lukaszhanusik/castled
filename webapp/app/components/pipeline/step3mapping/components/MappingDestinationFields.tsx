import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { useEffect, useState } from "react";
import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingImportantFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  const [optionalRow, setOptionalRow] = useState<JSX.Element[]>([]);
  const [addOptionalRow, setAddOptionalRow] = useState(true);

  useEffect(() => {
    addRow();
  }, []);

  /* 
  Keep tracking optional row and activate addRow 
  on Primary if optional row is empty. \/
  */
  useEffect(() => {
    console.log({ optionalRow });
    if (!optionalRow.length) {
      setAddOptionalRow(true);
    } else {
      setAddOptionalRow(false);
    }
  }, [optionalRow]);

  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  function addRow() {
    if (optionalFields && optionalFields.length) {
      setOptionalRow((prevState) => [...prevState, optionalFields[0]]);
      optionalFields.shift();
    }
  }

  // This is for primary key to add optional row if there are no optional row present.
  function addOptional() {
    if (addOptionalRow) {
      addRow();
      setAddOptionalRow(!addOptionalRow);
    }
  }

  function deleteRow(key: string) {
    // filter items based on key
    setOptionalRow((prevState) =>
      prevState.filter((item) => {
        return item.key !== key;
      })
    );
  }

  const optionalFields = destinationFieldSection[0].optionalFields?.map(
    (optionalField, i) => (
      <DestinationFieldRows
        key={optionalField.fieldName}
        options={options}
        destinationFieldSection={destinationFieldSection}
        isDisabled={!optionalField.optional}
        onChange={addRow}
        handleDelete={(e) => {
          e.preventDefault();
          deleteRow(optionalField.fieldName);
        }}
      />
    )
  );
  // console.log(optionalRow);

  return (
    <div className="row py-2">
      {destinationFieldSection.length > 0 &&
        destinationFieldSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.mandatoryFields!.length > 0 &&
              field.mandatoryFields?.map((mandatoryField) => (
                <DestinationFieldRows
                  key={mandatoryField.fieldName}
                  options={options}
                  defaultValue={{
                    value: mandatoryField.fieldName,
                    label: mandatoryField.fieldName,
                  }}
                  isDisabled={!mandatoryField.optional}
                  onChange={addOptional}
                />
              ))}
            {optionalRow}
          </WarehouseColumn>
        ))}
    </div>
  );
}

interface DestinationFieldRowsProps {
  options?: SchemaOptions[];
  destinationFieldSection?: MappingGroup[];
  defaultValue?: { value: string; label: string };
  isDisabled?: boolean;
  onChange?: (value: any) => void;
  handleDelete?: (value: any) => void;
}

function DestinationFieldRows({
  options,
  destinationFieldSection,
  defaultValue,
  isDisabled,
  onChange,
  handleDelete,
}: DestinationFieldRowsProps) {
  return (
    <tr>
      <th className="w-50">
        <Select options={options} onChange={onChange} />
      </th>
      <th className="w-50">
        <Select
          options={
            destinationFieldSection &&
            destinationFieldSection[0].optionalFields?.map((items: any) => {
              return { value: items.fieldName, label: items.fieldName };
            })
          }
          defaultValue={defaultValue}
          isDisabled={isDisabled}
        />
      </th>
      {!isDisabled && <button onClick={handleDelete}>X</button>}
    </tr>
  );
}
