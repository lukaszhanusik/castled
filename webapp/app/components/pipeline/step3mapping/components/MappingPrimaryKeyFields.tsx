import { Placeholder } from "react-bootstrap";
import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";
interface MappingPrimaryKeyFieldsProps extends MappingFieldsProps {
  onlyOptions?: SchemaOptions[];
}

export default function MappingPrimaryKeyFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  errors,
}: MappingPrimaryKeyFieldsProps) {
  // SECTION - 2 - Primary Keys to match the destination object
  const primaryKeysSection = mappingGroups.filter((fields) => {
    return fields.type === "PRIMARY_KEYS" && fields;
  });

  return (
    <div className="row py-2">
      {primaryKeysSection.length > 0 &&
        primaryKeysSection.map((field) => (
          <>
            <WarehouseColumn
              title={field.title}
              description={field.description}
            >
              <tr>
                <th className="col-6">
                  <Select
                    options={options}
                    onChange={(e) =>
                      setFieldValue?.(`PRIMARY_KEYS-warehouseField-0`, e?.value)
                    }
                    onBlur={() =>
                      setFieldTouched?.(`PRIMARY_KEYS-warehouseField-0`, true)
                    }
                    placeholder={"Select a column"}
                  />
                </th>
                <th className="col-6">
                  <Select
                    options={field.primaryKeys?.map((key) => ({
                      value: key.fieldName,
                      label: key.fieldDisplayName || key.fieldName,
                    }))}
                    onChange={(e) =>
                      setFieldValue?.(`PRIMARY_KEYS-appField-0`, e?.value)
                    }
                    onBlur={() =>
                      setFieldTouched?.(`PRIMARY_KEYS-appField-0`, true)
                    }
                    placeholder={"Select a field"}
                  />
                </th>
                <th className="col-2">
                  <Placeholder as="td">
                    <span className="required-icon">*</span>
                  </Placeholder>
                </th>
              </tr>
              <ErrorMessage errors={errors} include={"rimary"} />
            </WarehouseColumn>
          </>
        ))}
    </div>
  );
}
