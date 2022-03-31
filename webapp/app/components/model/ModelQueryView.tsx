import CodeInput from "../forminputs/CodeInput";

export default function ModelQueryView({ source }: { source?: string }) {
  return (
    <CodeInput
      editable={false}
      value={source ? source : ""}
      height="400px"
      className="border border-3"
    />
  );
}
