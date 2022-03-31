import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { sql } from "@codemirror/lang-sql";
import { FieldInputProps, FieldMetaProps } from "formik";

interface CodeInputProps {
  field: FieldInputProps<any>;
  meta?: FieldMetaProps<any>;
  onChange: ((value: string) => void) | undefined;
  props: any;
  optionsRef?: string;
  required?: boolean;
  className?: string;
  editable?: boolean;
  extension?: Extension[];
}

export default function CodeInput({
  field,
  meta,
  onChange,
  props,
  optionsRef,
  required,
  className,
  editable,
  extension,
}: CodeInputProps) {
  return (
    <CodeMirror
      {...field}
      {...props}
      value=""
      height={props.height ? props.height : "345px"}
      extensions={extension ? extension : [sql()]}
      onChange={onChange}
      className={className}
      editable={editable}
    />
  );
}
