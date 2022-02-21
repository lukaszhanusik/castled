import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingImportantFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  // SECTION - 3 - Other fields to match the destination object
  const destinationFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "DESTINATION_FIELDS" && fields;
  });

  return (
    <div className="row py-2">
      {destinationFieldSection.length > 0 &&
        destinationFieldSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.mandatoryFields!.length > 0 &&
              field.mandatoryFields?.map((mandatoryField) => (
                <tr>
                  <th>
                    <Select options={options} />
                  </th>
                  <th>
                    <input
                      className="form-control p-2 px-2"
                      value={mandatoryField.fieldName}
                      disabled={!mandatoryField.optional}
                    />
                  </th>
                </tr>
              ))}
            {field.optionalFields?.map((_optionalField) => (
              <tr>
                <th>
                  <Select options={options} />
                </th>
                <th>
                  <Select
                    options={field.optionalFields!.map((items) => {
                      return { value: items.fieldName, label: items.fieldName };
                    })}
                  />
                </th>
              </tr>
            ))}
          </WarehouseColumn>
        ))}
    </div>
  );
}
