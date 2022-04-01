import React from "react";
import LoadingConnector from "./LoadingConnector";

interface LoadingListProps {
    n: number,
    containerClass?: string,
}
const LoadingList = ({ n, containerClass }: LoadingListProps) => {
    let list = [];
    for (let i = 0; i < n; i++) {
        list.push(<LoadingConnector key={i} containerClass={containerClass}/>);
    }

    return <>{list}</>;
};

export default LoadingList;
