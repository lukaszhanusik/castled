import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import WarehouseColumn from "./Layouts/WarehouseColumn";
interface MappingPrimaryKeyFieldsProps extends MappingFieldsProps {
  onlyOptions?: SchemaOptions[];
}

export default function MappingTableBody({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
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
            {field.primaryKeys!.map((primaryKeys, index) => (
              <tr>
                <th className="w-50">
                  <Select
                    options={options}
                    onChange={(e) =>
                      setFieldValue?.(
                        `PRIMARY_KEYS-warehouseField-${index}`,
                        e?.value
                      )
                    }
                    onBlur={() =>
                      setFieldTouched?.(primaryKeys.fieldName, true)
                    }
                  />
                </th>
                <th className="w-50">
                  <Select
                    options={field.primaryKeys?.map((key) => ({
                      value: key.fieldName,
                      label: key.fieldName,
                    }))}
                    onChange={(e) =>
                      setFieldValue?.(
                        `PRIMARY_KEYS-appField-${index}`,
                        e?.value
                      )
                    }
                    onBlur={() =>
                      setFieldTouched?.(primaryKeys.fieldName, true)
                    }
                  />
                </th>
              </tr>
            ))}
          </WarehouseColumn>
        ))}
    </div>
  );
}
