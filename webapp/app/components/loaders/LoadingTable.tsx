import React from "react";
import { Table } from "react-bootstrap"
import Loading from "./Loading";

interface LoadingTableProps {
    headers?: string[],
}
const LoadingTable = ({ headers }: LoadingTableProps) => {
    if (!headers) {
        headers = ["", "", ""];
    }
    let loadingBody = ["", "", "", "", ""];
    return <div className="table-responsive loading-table mx-auto mt-2">
        <Table className="tr-collapse">
            <thead>
                <tr>
                    {headers && headers.map((header, idx) => (
                        <th key={idx}>{header}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {loadingBody.map((_) => <tr>
                    {headers && headers.map((header, idx) => (
                        <td key={idx}><Loading /></td>
                    ))}
                </tr>
                )}
            </tbody>
        </Table>
    </div>;
};

export default LoadingTable;
