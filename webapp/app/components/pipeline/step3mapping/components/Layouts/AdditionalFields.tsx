import { WarehouseSchema } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { useState } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../../types/componentTypes";

interface AdditionalFieldsProps extends MappingFieldsProps {
  type: string;
  // field: WarehouseSchema;
}

export default function AdditionalFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  type,
}: // field
AdditionalFieldsProps) {
  const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

  const renderBody = (
    <tr>
      <th className="w-50">
        <Select
          options={options}
          onChange={(e) => {
            setFieldValue?.("warehouseField", e);
            setWarehouseSelected(true);
          }}
          onBlur={() => setFieldTouched?.("warehouseField", true)}
        />
      </th>
      <th className="w-50">
        {type === "select" ? (
          <Select
            options={options}
            onChange={(e) => {
              setFieldValue?.("warehouseField", e);
              setWarehouseSelected(true);
            }}
            onBlur={() => setFieldTouched?.("warehouseField", true)}
          />
        ) : (
          <input
            type="text"
            placeholder="Enter a field"
            className="form-control p-2"
            onChange={(e) => setFieldValue?.("appField", e.target.value)}
            onBlur={() => setFieldTouched?.("appField", true)}
          />
        )}
      </th>
    </tr>
  );
  const addBody = [renderBody];

  if (warehouseSelected) {
    addBody.push(
      <AdditionalFields
        options={options}
        mappingGroups={mappingGroups}
        type={type}
        setFieldValue={setFieldValue}
        setFieldTouched={setFieldTouched}
      />
    );
  }
  return <>{addBody}</>;
}
