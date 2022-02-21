import { PrimaryKeyElement } from "@/app/common/dtos/PipelineSchemaResponseDto";
import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import WarehouseColumn from "./WarehouseColumn";
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

  // function appSchemaPrimaryKeysFilter(option: PrimaryKeyElement) {
  //   return [{ value: option.fieldName, label: option.fieldName }];
  // }

  return (
    <div className="row py-2">
      {primaryKeysSection.length > 0 &&
        primaryKeysSection.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            {field.primaryKeys!.map((key) => (
              <tr>
                <th>
                  <Select options={options} />
                </th>
                <th>
                  <input
                    className="form-control p-2 w-75 mx-2"
                    value={key.fieldName}
                    disabled={!key.optional}
                  />
                </th>
              </tr>
            ))}
          </WarehouseColumn>
        ))}
    </div>
  );
}
