import {
  ColumnDetail,
  ModelResponseDto,
} from "@/app/common/dtos/ModelResponseDto";
import { Table } from "react-bootstrap";
import { useState, useEffect, ChangeEvent, useMemo } from "react";
import { debounce } from "lodash";

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
          <td>{columnNameTransform(field.name)}</td>
          <td>
            <input
              type="text"
              value={stringTransform(field.schema.type)}
              disabled
            />
          </td>
          <td>{queryModelPK?.primaryKeys.includes(field.name) && "YES"}</td>
        </tr>
      ));

      setWarehouseRows(warehouseResults);
      setSearchResults(warehouseResults);
    }
  }, [warehouse]);

  const stringTransform = (str: string) => str[0] + str.slice(1).toLowerCase();

  const columnNameTransform = (str: string) =>
    str
      .split("_")
      .map((e) => e[0].toUpperCase() + e.slice(1))
      .join(" ");

  // used to delay processing of input change event if data is big
  const debounceHandler = useMemo(() => debounce(searchRows, 200), []);

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
          onChange={debounceHandler}
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
