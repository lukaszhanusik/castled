import { IconTrash } from "@tabler/icons";
import { useEffect } from "react";
import { Placeholder } from "react-bootstrap";
import Image from "react-bootstrap/Image";
import Select from "react-select";
import { DestinationFieldRowsProps } from "../../types/componentTypes";

export interface AdditionalFieldsProps
  extends Omit<DestinationFieldRowsProps, "defaultAppValue"> {
  inputChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  inputBlur: (e: React.ChangeEvent<HTMLInputElement>) => void;
  defaultAppValue?: string;
  pkRequired?: boolean;
  checkboxChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
  pkDisabled?: { [s: string]: boolean };
  name?: string;
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
  pkRequired,
  checkboxChange,
}: AdditionalFieldsProps) {
  return (
    <tr>
      <td className="col-5 p-2">
        <Select
          options={options}
          onChange={onChange}
          onBlur={onBlur}
          isClearable
          placeholder={"Select a column"}
          defaultValue={defaultWarehouseValue}
        />
      </td>
      <td className="P-2">
        <Image
          src="/images/arrow-right.svg"
          alt="Right Arrow for Mapping"
          // className="py-2"
        />
      </td>
      <td className="col-5 p-2">
        <input
          type="text"
          placeholder="Enter a field"
          className="form-control p-2"
          onChange={inputChange}
          onBlur={inputBlur}
          defaultValue={defaultAppValue}
        />
      </td>
      {pkRequired && (
        <td className="col-2 p-2 text-center align-middle">
          <input type="checkbox" className="me-2" onChange={checkboxChange} />
        </td>
      )}
      <Placeholder as="td" className="text-center">
        <IconTrash size={16} onClick={handleDelete} className="delete-btn" />
      </Placeholder>
    </tr>
  );
}
