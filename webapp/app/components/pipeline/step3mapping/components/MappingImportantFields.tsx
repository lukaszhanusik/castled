import InputSelect from "@/app/components/forminputs/InputSelect";
import { values } from "lodash";
import { MappingFieldsProps } from "../types/componentTypes";

export default function MappingImportantFields({
  title,
  description,
  options,
  setFieldValue,
  setFieldTouched,
  fieldName,
}: MappingFieldsProps) {
  return (
    <div className="flex-column align-self-center my-2">
      <InputSelect
        title={title}
        options={options}
        deps={undefined}
        values={values}
        setFieldValue={setFieldValue}
        setFieldTouched={setFieldTouched}
        name={fieldName + ".appField"}
      />
    </div>
  );
}
