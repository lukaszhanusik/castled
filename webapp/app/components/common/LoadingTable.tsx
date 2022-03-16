import React from "react";
import { Table } from "react-bootstrap"

const LoadingTable = () => {
    return <div className="table-responsive mx-auto mt-1">
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
    </div>;
};

export default LoadingTable;
