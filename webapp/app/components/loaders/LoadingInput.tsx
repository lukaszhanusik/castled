import React from "react";

import Loading from "./Loading";
interface LoadingInputProps {
    n?: number
}

const LoadingInput = ({ n }: LoadingInputProps) => {
    let InputList = [];
    if (n) {
        for (let i = 0; i < n; i++) {
            InputList.push(<div className="my-5" key={i}>
                <Loading className="w-25 mt-2" />
                <input className="form-control opacity-50 border-0 linear-background mt-2 py-3" />
            </div>);
        }
    }
    else {
        InputList.push(<div className="my-5">
            <Loading className="w-25 mt-2" />
            <input className="form-control opacity-50 border-0 linear-background mt-2 py-3" />
        </div>)
    }
    return <>{InputList}</>;
};

export default LoadingInput;
