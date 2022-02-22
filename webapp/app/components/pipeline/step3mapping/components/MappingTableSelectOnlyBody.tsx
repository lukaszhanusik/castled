import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";
interface MappingPrimaryKeyFieldsProps extends MappingFieldsProps {
  onlyOptions?: SchemaOptions[];
}

export default function MappingTableBody({
  options,
  mappingGroups,
}: MappingPrimaryKeyFieldsProps) {
  // SECTION - 2 - Primary Keys to match the destination object
  const primaryKeysSection = mappingGroups.filter((fields) => {
    return fields.type === "PRIMARY_KEYS" && fields;
  });

  return (
    <div className="row py-2">
      {primaryKeysSection.length > 0 &&
        primaryKeysSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.primaryKeys!.map((_e) => (
              <tr>
                <th className="w-50">
                  <Select options={options} />
                </th>
                <th className="w-50">
                  <Select
                    options={field.primaryKeys?.map((key) => ({
                      value: key.fieldName,
                      label: key.fieldName,
                    }))}
                  />
                </th>
              </tr>
            ))}
          </WarehouseColumn>
        ))}
    </div>
  );
}
