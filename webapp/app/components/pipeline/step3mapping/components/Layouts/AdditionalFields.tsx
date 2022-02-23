import { WarehouseSchema } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { useState } from "react";
import Select from "react-select";
import { MappingFieldsProps } from "../../types/componentTypes";

interface AdditionalFieldsProps extends MappingFieldsProps {
  additionalRow: JSX.Element[];
  addRow: () => void;
}

export default function AdditionalFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  additionalRow,
  addRow
}: // field
AdditionalFieldsProps) {
  // const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);
  
  return (
    <tr>
      <th className="w-50">
        <Select
          options={options}
          onChange={(e) => {
            addRow();
            setFieldValue?.(`warehouseField-${additionalRow.length}`, e?.value);
            // setWarehouseSelected(true);
          }}
          onBlur={() =>
            setFieldTouched?.(`warehouseField-${additionalRow.length}`, true)
          }
        />
      </th>
      <th className="w-50">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
          onChange={(e) =>
            setFieldValue?.(`appField-${additionalRow.length}`, e.target.value)
          }
          onBlur={() =>
            setFieldTouched?.(`appField-${additionalRow.length}`, true)
          }
        />
      </th>
    </tr>
  );
  // const addBody = [renderBody];

  // if (warehouseSelected) {
  //   addBody.push(
  //     <AdditionalFields
  //       options={options}
  //       mappingGroups={mappingGroups}
  //       setFieldValue={setFieldValue}
  //       setFieldTouched={setFieldTouched}
  //     />
  //   );
  // }
  // return <>{renderBody}</>;
}
