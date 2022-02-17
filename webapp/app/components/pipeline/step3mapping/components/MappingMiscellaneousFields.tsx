import { Table } from "react-bootstrap";
import { MappingFieldsProps } from "../types/componentTypes";
import MappingTableBody from "./MappingTableBody";

export default function MappingMiscellaneousFields({
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
        <Table hover>
          <thead>
            <tr>
              <th>Warehouse Column</th>
              <th>App Column</th>
            </tr>
          </thead>
          <tbody>
            <MappingTableBody options={options} />
          </tbody>
        </Table>
      </div>
    </div>
  );
}