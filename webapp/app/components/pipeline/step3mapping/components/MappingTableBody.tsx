import InputField from "@/app/components/forminputs/InputField";
import InputSelect from "@/app/components/forminputs/InputSelect";
import { values } from "lodash";
import { useState } from "react";
import { Placeholder } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";

export default function MappingTableBody({
  title,
  description,
  options,
  setFieldValue,
  setFieldTouched,
  fieldName,
}: MappingFieldsProps) {
  // const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

  // const change = (e: any) => {
  //   setWarehouseSelected(true)
  //   console.log("change")
  // }

  const renderBody = (
    <tr>
      <th>
        <InputSelect
          title={undefined}
          options={options}
          deps={undefined}
          values={values}
          setFieldValue={setFieldValue}
          setFieldTouched={setFieldTouched}
          name={fieldName + ".appField"}
        />
      </th>
      <th>
        <InputField
          className="w-100"
          type="text"
          title={undefined}
          values={values}
          setFieldValue={setFieldValue}
          setFieldTouched={setFieldTouched}
          name={fieldName + ".appField"}
        />
      </th>
    </tr>
  );
  const addBody = [renderBody];

  // if (warehouseSelected) {
  //   addBody.push(
  //     <MappingTableBody
  //       options={options}
  //       title={undefined}
  //       setFieldValue={setFieldValue}
  //       setFieldTouched={setFieldTouched}
  //       fieldName={fieldName}
  //     />
  //   );
  // }

  return <>{addBody}</>;
}
