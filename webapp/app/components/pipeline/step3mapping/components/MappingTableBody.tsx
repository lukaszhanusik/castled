import { useState } from 'react';
import Select from 'react-select';
import {MappingFieldsProps} from "../types/componentTypes";

export default function MappingTableBody({ options }: Pick<MappingFieldsProps, "options">) {
  const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);
  const [appSelected, setAppSelected] = useState<string>("");

  const renderBody = (
    <tr>
      <th>
        <Select options={options} onChange={() => setWarehouseSelected(true)} />
      </th>
      <th>
        <input
          type="text"
          placeholder="Enter a field"
          onChange={(e) => setAppSelected(e.target.value)}
        />
      </th>
    </tr>
  );
  const addBody = [renderBody];

  if (warehouseSelected && appSelected) {
    addBody.push(<MappingTableBody options={options}/>);
  }

  return <>{addBody}</>;
}