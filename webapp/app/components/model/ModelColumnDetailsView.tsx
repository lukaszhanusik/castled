import {
  ColumnDetail,
  ModelResponseDto,
} from "@/app/common/dtos/ModelResponseDto";
import { Table } from "react-bootstrap";
import { useState, useEffect, ChangeEvent } from "react";

type ModelColumnDetailsViewProps = Partial<
  Pick<ModelResponseDto, "warehouse" | "queryModelPK">
>;

function ModelColumnDetailsView({
  warehouse,
  queryModelPK,
}: ModelColumnDetailsViewProps) {
  const [warehouseRows, setWarehouseRows] = useState<JSX.Element[]>([]);
  const [searchResults, setSearchResults] = useState<JSX.Element[]>([]);

  useEffect(() => {
    if (warehouse && warehouse.columnDetails) {
      const warehouseResults = warehouse.columnDetails.map((field, index) => (
        <tr key={`${field.name}-${index}`}>
          <td>{field.name}</td>
          <td>
            <input type="text" value={pascalCase(field.schema.type)} disabled />
          </td>
          <td>{queryModelPK?.primaryKeys.includes(field.name) && "YES"}</td>
        </tr>
      ));

      setWarehouseRows((prev) => [...prev, ...warehouseResults]);
      setSearchResults((prev) => [...prev, ...warehouseResults]);
    }
  }, [warehouse]);

  const pascalCase = (str: string) => str[0] + str.slice(1).toLowerCase();

  function searchRows(e: ChangeEvent<HTMLInputElement>) {
    setSearchResults(
      warehouseRows.filter((item) =>
        item.key?.toString().includes(e.target.value)
      )
    );
  }
  return (
    <>
      <div className="text-center">
        <h2 className="mb-1 mt-4 font-weight-bold">Columns</h2>
        <p className="mb-1 mt-2">
          All the warehouse columns used in the query associated with the model.
        </p>
      </div>
      <div>
        <input
          type="text"
          className="mb-1 mt-4"
          placeholder="Search Column"
          onChange={(e) => searchRows(e)}
        />
      </div>
      <Table hover>
        <thead>
          <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Primary Key</th>
          </tr>
        </thead>
        <tbody>{searchResults}</tbody>
      </Table>
    </>
  );
}

export default ModelColumnDetailsView;
