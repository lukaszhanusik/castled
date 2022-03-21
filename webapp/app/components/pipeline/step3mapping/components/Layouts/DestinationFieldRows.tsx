import { IconTrash } from "@tabler/icons";
import { Placeholder } from "react-bootstrap";
import Image from "react-bootstrap/Image";
import Select from "react-select";
import { DestinationFieldRowsProps } from "../../types/componentTypes";

export default function DestinationFieldRows({
  options,
  destinationFieldSection,
  defaultWarehouseValue,
  defaultAppValue,
  isDisabled,
  onChangeWarehouse,
  onChangeAppField,
  onBlur,
  handleDelete,
  values,
  isClearable,
}: DestinationFieldRowsProps) {
  return (
    <tr>
      <td className="col-6">
        <Select
          options={options}
          onChange={onChangeWarehouse}
          onBlur={onBlur}
          isClearable={isClearable}
          placeholder={"Select a column"}
          defaultValue={defaultWarehouseValue}
        />
      </td>
      <td>
        <Image
          src="/images/arrow-right.svg"
          alt="Right Arrow for Mapping"
          className="py-2"
        />
      </td>
      <td className="col-6">
        <Select
          options={
            destinationFieldSection &&
            destinationFieldSection.optionalFields?.map((items: any) => {
              return {
                value: items.fieldName,
                label: items.fieldDisplayName || items.fieldName,
              };
            })
          }
          defaultValue={defaultAppValue}
          isDisabled={isDisabled}
          onBlur={onBlur}
          isClearable={isClearable}
          onChange={onChangeAppField}
          placeholder={"Select a field"}
        />
      </td>
      {isDisabled ? (
        <Placeholder as="td" className="pb-0">
          <label className="required-icon">*</label>
        </Placeholder>
      ) : (
        <Placeholder as="td">
          <IconTrash onClick={handleDelete} className="delete-btn" />
        </Placeholder>
      )}
    </tr>
  );
}
