import React, { useEffect, useState } from "react";
import { FieldInputProps, FieldMetaProps, useField } from "formik";
import cn from "classnames";
import { InputBaseProps } from "@/app/common/dtos/InputBaseProps";
import { AxiosResponse } from "axios";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import TextareaAutosize from "react-textarea-autosize";

export interface InputFieldProps extends InputBaseProps {
  type: string;
  minRows?: number;
  optionsRef?: string;
  dataFetcher?: (
    optionsRef: string
  ) => Promise<AxiosResponse<DataFetcherResponseDto>>;
}

const InputField = ({
  title,
  required,
  description,
  className,
  onChange,
  setFieldValue,
  optionsRef,
  dataFetcher,
  ...props
}: InputFieldProps) => {
  const [field, meta] = useField(props);
  const isHidden = props.type === "hidden";
  const [loading, setLoading] = useState(false);
  useEffect(() => {
    if (optionsRef) {
      setLoading(true);
      dataFetcher?.(optionsRef).then(({ data }) => {
        setFieldValue(field.name, data.options[0].value);
        setLoading(false);
      });
    }
  }, [optionsRef]);
  return (
    <div className={className ? className : cn({ "mb-3": !isHidden })}>
      {title && !isHidden && (
        <label htmlFor={props.id || props.name} className="form-label">
          {required && <span className="required-icon">*</span>}
          {title}
        </label>
      )}
      {getInput(field, meta, onChange, props, optionsRef, required)}
      {loading && !isHidden && (
        <div className="spinner-border spinner-border-sm"></div>
      )}
      {meta.touched && meta.error ? (
        <div className="error">{meta.error}</div>
      ) : null}
    </div>
  );
};

function getInput(
  field: FieldInputProps<any>,
  meta: FieldMetaProps<any>,
  onChange: ((value: string) => void) | undefined,
  props: any,
  optionsRef?: string,
  required?: boolean
) {
  if (props.type === "textarea") {
    return (
      <TextareaAutosize
        {...field}
        {...props}
        onChange={(e) => {
          field.onChange(e);
          onChange?.(e.currentTarget.value);
        }}
        className={cn(props.inputClassName, "form-control", {
          "required-field": meta.touched && meta.error,
        })}
        defaultValue={field.value}
        required={required}
      />
    );
  } else {
    return (
      <input
        onChange={(e) => {
          field.onChange(e);
          onChange?.(e.currentTarget.value);
        }}
        onBlur={field.onBlur}
        {...props}
        className={cn(props.inputClassName, "form-control", {
          "required-field": meta.touched && meta.error,
        })}
        value={field.value}
        disabled={optionsRef}
      />
    );
  }
}

export default InputField;
