import React from "react";
import { ListGroup, Badge } from "react-bootstrap";

import Loading from "./Loading";
interface LoadingConnectorProps {
    containerClass?: string,
}

const LoadingConnector = ({containerClass} : LoadingConnectorProps) => {
    return<ListGroup>
        < ListGroup.Item
            className={`rounded loading-connector ${containerClass}`}
        >
            <div className="row p-2 align-items-center">
                <div className="col-4 loading-circle"></div>
                <strong className="col-8"><Loading /></strong>
            </div>
        </ListGroup.Item >
    </ListGroup >;
};

export default LoadingConnector;
