import { ModelResponseDto } from "@/app/common/dtos/ModelResponseDto";
import { Table } from "react-bootstrap";
import { useState, useEffect, ChangeEvent, useMemo } from "react";
import { debounce } from "lodash";

type ModelColumnDetailsViewProps = Partial<
  Pick<ModelResponseDto, "columnDetails" | "queryPK">
>;

function ModelColumnDetailsView({
  columnDetails,
  queryPK,
}: ModelColumnDetailsViewProps) {
  const [warehouseRows, setWarehouseRows] = useState<JSX.Element[]>([]);
  const [searchResults, setSearchResults] = useState<JSX.Element[]>([]);

  useEffect(() => {
    if (columnDetails) {
      const warehouseResults = columnDetails.map((field, index) => (
        <tr key={`${field.name}-${index}`}>
          <td className="col-4">{columnNameTransform(field.name)}</td>
          <td className="col-6">
            <input
              type="text"
              className="px-2 py-1 w-75"
              value={stringTransform(field.schema.type)}
              disabled
            />
          </td>
          <td className="col-2">
            {queryPK?.primaryKeys.includes(field.name) && (
              <div className="d-flex align-items-center">
                <img
                  className="status-mark"
                  src="/images/check-good-mark.svg"
                />
              </div>
            )}
          </td>
        </tr>
      ));

      setWarehouseRows(warehouseResults);
      setSearchResults(warehouseResults);
    }
  }, [columnDetails]);

  const stringTransform = (str: string) => str[0] + str.slice(1).toLowerCase();

  const columnNameTransform = (str: string) =>
    str
      .split("_")
      .map((e) => e[0].toUpperCase() + e.slice(1))
      .join(" ");

  // used to delay processing of input change event if data is big
  // const debounceHandler = useMemo(() => debounce(searchRows, 200), []);

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
      <div className="container py-4">
        <div className="row height d-flex justify-content-center align-items-center">
          <div className="col-md-8">
            <div className="search">
              <input
                type="text"
                className="form-control search-input"
                placeholder="Search Column"
                onChange={searchRows}
              />
            </div>
          </div>
        </div>
      </div>
      <div className="container">
        <Table>
          <thead>
            <tr>
              <th>NAME</th>
              <th>TYPE</th>
              <th>PRIMARY KEY</th>
            </tr>
          </thead>
          <tbody>{searchResults}</tbody>
        </Table>
      </div>
    </>
  );
}

export default ModelColumnDetailsView;
