import React from "react";
import { Table } from "react-bootstrap"
import Loading from "./Loading";

interface LoadingTableProps {
    headers?: string[],
    n?: number,
    isMapping?: Boolean,
}
const LoadingTable = ({ headers, n, isMapping }: LoadingTableProps) => {
    if (!headers) {
        headers = ["", "", ""];
    }
    if(n === undefined){
        n = 4;
    }
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
                {Array.from({length: n}, (_) => <tr>
                    {headers && headers.map((header, idx) => (
                        <td key={idx} className={`${isMapping && idx === 1 ? "text-center w-25 px-5" : ""}`}><Loading /></td>
                    ))}
                </tr>
                )}
            </tbody>
        </Table>
    </div>;
};

export default LoadingTable;
