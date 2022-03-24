import React from "react";
import { Table } from "react-bootstrap"
import Loading from "./Loading";

interface LoadingTableProps {
    headers?: string[],
}
const LoadingTable = ({ headers }: LoadingTableProps) => {
    if (!headers) {
        headers = ["", "", "", ""];
    }
    let loadingBody = ["", "", ""];
    return <div className="table-responsive mx-auto mt-2">
        <Table className="tr-collapse">
            <thead>
                <tr className="pt-4 pb-4">
                    {headers && headers.map((header, idx) => (
                        <th key={idx} className="p-2">{header}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {loadingBody.map((_) => <tr className="pt-4 pb-4">
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
