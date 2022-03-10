import { Table } from "react-bootstrap";

interface WarehouseColumnProps {
  title: string;
  description: string;
  pkRequired?: boolean;
  children: React.ReactNode;
}

export default function WarehouseColumn({
  title,
  description,
  pkRequired,
  children,
}: WarehouseColumnProps) {
  return (
    <>
      <div className="flex-column align-self-center">
        <div className="flex-column mx-4">
          <label className="row py-1">{title}</label>
          <label className="row description text-muted pb-3">{description}</label>
        </div>
        <div>
          <Table>
            <thead>
              <tr>
                <th>Warehouse Column</th>
                <th></th>
                <th>App Field</th>
                {pkRequired && <th>Primary Key</th>}
                <th></th>
              </tr>
            </thead>
            <tbody>{children}</tbody>
          </Table>
        </div>
      </div>
    </>
  );
}
