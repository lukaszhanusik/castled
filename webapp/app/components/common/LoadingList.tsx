import React from "react";
import LoadingConnector from "./LoadingConnector";

interface LoadingListProps {
    n: number
}
const LoadingList = ({ n }: LoadingListProps) => {
    let list = [];
    for (let i = 0; i < n; i++) {
        list.push(<LoadingConnector key={i}/>);
    }

    return <>{list}</>;
};

export default LoadingList;
