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
    <div className="flex-column align-self-center py-2">
      <div className="flex-column mx-4">
        <div className="row py-1 font-weight-bold">{title}</div>
        <div className="row description text-muted pb-3">{description}</div>
      </div>
      <div>
        <Table hover>
          <thead>
            <tr>
              <th>Warehouse Column</th>
              <th>App Column</th>
            </tr>
          </thead>
          <tbody>{children}</tbody>
        </Table>
      </div>
    </div>
  );
}
