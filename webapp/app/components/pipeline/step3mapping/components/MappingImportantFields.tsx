import Select from "react-select/src/Select";
import { MappingFieldsProps } from "../types/componentTypes";

export default function MappingImportantFields({
  title,
  description,
  options,
}: MappingFieldsProps) {
  return (
    <div className="flex-column align-self-center">
      <div className="flex-column mx-4">
        <div className="row">{title}</div>
        <div className="row description text-muted">{description}</div>
      </div>
      <div>
        <Select options={options} />
      </div>
    </div>
  );
}