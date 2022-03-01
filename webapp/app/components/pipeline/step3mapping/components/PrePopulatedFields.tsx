import Select from "react-select";
import { AdditionalFieldsProps } from "./MappingMiscellaneousFields";
import { IconTrash } from "@tabler/icons";
import { Button, Placeholder } from "react-bootstrap";
import Image from "react-bootstrap/image";

interface PrePopulatedFieldsProps
  extends Omit<AdditionalFieldsProps, "defaultValue"> {
  inputDefaultValue?: string;
  selectDefaultValue?: { value: string; label: string };
}
export default function PrePopulatedFields({
  options,
  values,
  onChange,
  onBlur,
  handleDelete,
  inputChange,
  inputBlur,
  inputDefaultValue,
  selectDefaultValue,
}: PrePopulatedFieldsProps) {
  return (
    <tr>
      <th className="col-6">
        <Select
          options={options}
          onChange={onChange}
          onBlur={onBlur}
          isClearable
          placeholder={"Select a column"}
          defaultValue={selectDefaultValue}
        />
      </th>
      <th>
        <Image
          src="/images/arrow-right.svg"
          alt="Right Arrow for Mapping"
          className="py-2"
        />
      </th>
      <th className="col-6">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
          onChange={inputChange}
          onBlur={inputBlur}
          defaultValue={inputDefaultValue}
        />
      </th>
      <Placeholder as="td">
        <IconTrash onClick={handleDelete} className="delete-btn" />
      </Placeholder>
    </tr>
  );
} 