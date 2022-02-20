import Select from "react-select";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";

type MappingFieldsOptions = Pick<MappingFieldsProps, "options">;

interface MappingFieldsOptionsProps {
  options: SchemaOptions[];
  onlyOptions?: SchemaOptions[];
}

export default function MappingTableBody({
  options,
  onlyOptions,
}: MappingFieldsOptionsProps) {
  return (
    <tr>
      <th>
        <Select options={options} />
      </th>
      <th>
        <Select options={onlyOptions} />
      </th>
    </tr>
  );
}
