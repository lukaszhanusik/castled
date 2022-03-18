import { Table } from "react-bootstrap";

export default function LoadingTable() {
  return (
    <div>
      <div className="table-responsive mx-auto loading-table">
        <Table>
          <tbody>
            <tr className="pt-4 pb-4">
              <td>
                <div className="linear-background"></div>
              </td>
              <td>
                <div className="linear-background"></div>
              </td>
              <td>
                <div className="linear-background"></div>
              </td>
              <td>
                <div className="linear-background"></div>
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    </div>
  );
}
