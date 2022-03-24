import React from "react";
import { ListGroup, Badge } from "react-bootstrap";

import Loading from "./Loading";

const LoadingConnector = () => {
    return<ListGroup>
        < ListGroup.Item
            className="rounded"
        >
            <div className="row p-2 align-items-center">
                <div className="col-4 loading-circle"></div>
                <strong className="col-8"><Loading /></strong>
            </div>
        </ListGroup.Item >
    </ListGroup >;
};

export default LoadingConnector;
