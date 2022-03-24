import { Placeholder, Table } from "react-bootstrap";

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
        <div className="flex-column">
          <label className="py-1">{title}</label>
          <div className="description text-muted pb-3">{description}</div>
        </div>
        <div>
          <Table>
            <thead>
              <tr>
                <th>Warehouse Column</th>
                <th></th>
                <th>App Field</th>
                {pkRequired && (
                  <th>
                    Primary Key <label className="required-icon">*</label>
                  </th>
                )}
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
