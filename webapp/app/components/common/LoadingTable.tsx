import React from "react";
import { Table } from "react-bootstrap"
import Loading from "./Loading";

const LoadingTable = () => {
    return <div className="table-responsive mx-auto mt-1">
        <Table>
            <tbody>
                <tr className="pt-4 pb-4">
                    <td>
                        <Loading />
                    </td>
                    <td>
                        <Loading />
                    </td>
                    <td>
                        <Loading />
                    </td>
                    <td>
                        <Loading />
                    </td>
                </tr>
            </tbody>
        </Table>
    </div>;
};

export default LoadingTable;
