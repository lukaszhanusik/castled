import { Table } from "react-bootstrap";

interface WarehouseColumnProps {
  title: string;
  description: string;
  children: React.ReactNode;
}

export default function WarehouseColumn({
  title,
  description,
  children,
}: WarehouseColumnProps) {
  return (
    <>
      <div className="flex-column align-self-center">
        <div className="flex-column mx-4">
          <label className="row py-1 font-weight-bold">{title}</label>
          <label className="row description text-muted pb-3">{description}</label>
        </div>
        <div>
          <Table hover>
            <thead>
              <tr>
                <th>Warehouse Column</th>
                <th>App Field</th>
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
