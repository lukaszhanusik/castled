import { MappingFieldsProps } from "../types/componentTypes";
import AdditionalFields from "./Layouts/AdditionalFields";
import WarehouseColumn from "./Layouts/WarehouseColumn";

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
}: MappingFieldsProps) {
  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            <AdditionalFields
              options={options}
              mappingGroups={mappingGroups}
            />
          </WarehouseColumn>
        ))}
    </div>
  );
}

// import InputField from "@/app/components/forminputs/InputField";
// import InputSelect from "@/app/components/forminputs/InputSelect";
// import { values } from "lodash";
// import { useState } from "react";
// import { Placeholder } from "react-bootstrap";
// import { MappingFieldsProps } from "../types/componentTypes";

// export default function MappingTableBody({
//   title,
//   description,
//   options,
//   setFieldValue,
//   setFieldTouched,
//   fieldName,
// }: MappingFieldsProps) {
//   const [warehouseSelected, setWarehouseSelected] = useState<boolean>(false);

//   const renderBody = (
//     <tr>
//       <th>
//         <InputSelect
//           title={undefined}
//           options={options}
//           deps={undefined}
//           values={values}
//           setFieldValue={setFieldValue}
//           setFieldTouched={setFieldTouched}
//           name={fieldName + ".appField"}
//           addColumn={() => setWarehouseSelected(true)}
//         />
//       </th>
//       <th>
//         <InputField
//           className="w-100"
//           type="text"
//           title={undefined}
//           values={values}
//           setFieldValue={setFieldValue}
//           setFieldTouched={setFieldTouched}
//           name={fieldName + ".appField"}
//         />
//       </th>
//     </tr>
//   );
//   const addBody = [renderBody];

//   if (warehouseSelected) {
//     addBody.push(
//       <MappingTableBody
//         options={options}
//         title={undefined}
//         setFieldValue={setFieldValue}
//         setFieldTouched={setFieldTouched}
//         fieldName={fieldName}
//       />
//     );
//   }

//   return <>{addBody}</>;
// }
