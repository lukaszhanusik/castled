import { useState } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../../types/componentTypes";

interface AdditionalFieldsProps extends MappingFieldsProps {
  type: string;
}

export default function AdditionalFields({
  options,
  mappingGroups,
  type,
}: AdditionalFieldsProps) {
  const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

  const renderBody = (
    <tr>
      <th className="w-50">
        <Select options={options} onChange={() => setWarehouseSelected(true)} />
      </th>
      <th className="w-50">
        {type === "select" ? (
          <Select
            options={options}
            onChange={() => setWarehouseSelected(true)}
          />
        ) : (
          <input
            type="text"
            placeholder="Enter a field"
            className="form-control p-2"
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
      />
    );
  }
  return <>{addBody}</>;
}
