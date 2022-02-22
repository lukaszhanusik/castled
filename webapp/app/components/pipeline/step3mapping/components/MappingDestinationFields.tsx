import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { useState } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingImportantFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  const optionalFields = destinationFieldSection[0].optionalFields?.map(
    (_optionalField) => (
      <tr>
        <th className="w-50">
          <Select options={options} />
        </th>
        <th className="w-50">
          <Select
            options={destinationFieldSection[0].optionalFields!.map((items) => {
              return { value: items.fieldName, label: items.fieldName };
            })}
          />
        </th>
      </tr>
    )
  );

  return (
    <div className="row py-2">
      {destinationFieldSection.length > 0 &&
        destinationFieldSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.mandatoryFields!.length > 0 &&
              field.mandatoryFields?.map((mandatoryField) => (
                <tr>
                  <th className="w-50">
                    <Select options={options} />
                  </th>
                  <th className="w-50">
                    <Select
                      // className="form-control p-2 px-2"
                      defaultValue={{
                        value: mandatoryField.fieldName,
                        label: mandatoryField.fieldName,
                      }}
                      isDisabled={!mandatoryField.optional}
                    />
                  </th>
                </tr>
              ))}
            {}
          </WarehouseColumn>
        ))}
    </div>
  );
}

interface DestinationFieldRowsProps {
  options?: SchemaOptions[];
  destinationFieldSection?: MappingGroup[];
  defaultValue?: {value: string; label: string};
  isDisabled?: boolean;
}

function DestinationFieldRows({
  options,
  destinationFieldSection,
  defaultValue,
  isDisabled,
}: DestinationFieldRowsProps) {
  return (
    <tr>
      <th className="w-50">
        <Select options={options} />
      </th>
      <th className="w-50">
        <Select
          options={destinationFieldSection}
          defaultValue={defaultValue}
          isDisabled={!isDisabled}
        />
      </th>
    </tr>
  );
}
