import CodeMirror, { Extension } from "@uiw/react-codemirror";
import { sql } from "@codemirror/lang-sql";
import { FieldInputProps, FieldMetaProps } from "formik";

interface CodeInputProps {
  field?: FieldInputProps<any>;
  meta?: FieldMetaProps<any>;
  onChange?: ((value: string) => void) | undefined;
  props?: any;
  optionsRef?: string;
  className?: string;
  editable?: boolean;
  extension?: Extension[];
  height?: string;
  value?: string;
  minHeight?: string;
}

export default function CodeInput({
  field,
  meta,
  onChange,
  props,
  optionsRef,
  className,
  editable,
  extension,
  value,
  height,
  minHeight,
}: CodeInputProps) {
  return (
    <CodeMirror
      {...field}
      {...props}
      value={value ? value : ""}
      height={height ? height : "auto"}
      minHeight={minHeight}
      extensions={extension ? extension : [sql()]}
      onChange={onChange}
      className={className}
      editable={editable}
    />
  );
}
