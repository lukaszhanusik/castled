import { IconTrash } from "@tabler/icons";
import { Placeholder } from "react-bootstrap";
import Image from "react-bootstrap/Image";
import Select from "react-select";
import { DestinationFieldRowsProps } from "../../types/componentTypes";

export interface AdditionalFieldsProps
  extends Omit<DestinationFieldRowsProps, "defaultAppValue"> {
  inputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  inputBlur: (e: React.ChangeEvent<HTMLInputElement>) => void;
  defaultAppValue?: string;
}

export function AdditionalFields({
  options,
  values,
  onChange,
  onBlur,
  handleDelete,
  inputChange,
  inputBlur,
  defaultWarehouseValue,
  defaultAppValue,
}: AdditionalFieldsProps) {
  return (
    <tr>
      <th className="col-6">
        <Select
          options={options}
          onChange={onChange}
          onBlur={onBlur}
          isClearable
          placeholder={"Select a column"}
          defaultValue={defaultWarehouseValue}
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
          defaultValue={defaultAppValue}
        />
      </th>
      <Placeholder as="td">
        <IconTrash onClick={handleDelete} className="delete-btn" />
      </Placeholder>
    </tr>
  );
}
