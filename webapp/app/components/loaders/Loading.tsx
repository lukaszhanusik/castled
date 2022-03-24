import React from "react";
import cn from "classnames";

interface LoadingProps {
  className?: string;
}

const Loading = ({ className }: LoadingProps) => {
  return <div className={cn(className, "linear-background")}></div>;
};

export default Loading;
