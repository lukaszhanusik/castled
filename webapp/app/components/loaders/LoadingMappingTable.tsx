import { IconTrash } from "@tabler/icons";
import React from "react";
import { FormCheck, Image, Table } from "react-bootstrap"
import Loading from "./Loading";
import LoadingInput from "./LoadingInput";

interface LoadingTableProps {
    headers?: string[],
}
const LoadingTable = ({ headers }: LoadingTableProps) => {
    if (!headers) {
        headers = ["Warehouse Column","", "App field", "Primary Key", ""];
    }
    let loadingBody = ["", "", "", ""];
    return <div className="container table-responsive loading-table mx-auto mt-2">
        <Table className="tr-collapse">
            <thead>
                <tr>
                    {headers && headers.map((header, idx) => (
                        <th key={idx}>{header}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
                {loadingBody.map((_, idx) => <tr key={idx}>
                    <td><input className="form-control linear-background py-3" /></td>
                    <td> <Image src="/images/arrow-right.svg" /> </td>
                    <td><input className="form-control linear-background py-3" /></td>
                    <td><FormCheck className="text-center" disabled /></td>
                    <td className="p-1"><IconTrash size={16} className="delete-btn" /></td>
                </tr>
                )}
            </tbody>
        </Table>
    </div>;
};

export default LoadingTable;
