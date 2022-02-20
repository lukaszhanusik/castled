import Select from "react-select";
import { MappingFieldsProps } from "../types/componentTypes";

export default function MappingImportantFields({
  title,
  description,
  options,
}: MappingFieldsProps) {
  return (
    <div className="flex-column align-self-center my-2">
      <div className="flex-column mx-4 my-2">
        <div className="row font-weight-bold">{title}</div>
        <div className="row description text-muted">{description}</div>
      </div>
      <div>
        <Select options={options} />
      </div>
    </div>
  );
}