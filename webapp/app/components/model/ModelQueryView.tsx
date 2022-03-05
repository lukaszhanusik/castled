import TextareaAutosize from "react-textarea-autosize";

export default function ModelQueryView({ source }: { source?: string }) {
  return (
    <TextareaAutosize
      minRows={20}
      maxRows={25}
      value={source}
      disabled
      className="w-100 p-3"
    />
  );
}
