import { useState } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../../types/componentTypes";

export default function AdditionalFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

  const renderBody = (
    <tr>
      <th>
        <Select options={options} onChange={() => setWarehouseSelected(true)} />
      </th>
      <th>
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
        />
      </th>
    </tr>
  );
  const addBody = [renderBody];

  if (warehouseSelected) {
    addBody.push(
      <AdditionalFields options={options} mappingGroups={mappingGroups} />
    );
  }
  return <>{addBody}</>;
}
