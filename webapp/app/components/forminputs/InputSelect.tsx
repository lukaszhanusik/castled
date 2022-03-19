import React, { useEffect, useState } from "react";
import { FieldInputProps, useField } from "formik";
import { InputBaseProps } from "@/app/common/dtos/InputBaseProps";
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import _, { values } from "lodash";
import { AxiosResponse } from "axios";
import { ObjectUtils } from "@/app/common/utils/objectUtils";
import { Spinner } from "react-bootstrap";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import Select, { components } from "react-select";
import cn from "classnames";
import { IconRefresh } from "@tabler/icons";

export interface InputSelectOptions extends InputBaseProps {
  options: SelectOptionDto[] | undefined;
  values: any;
  dValues?: any[];
  setFieldValue: (field: string, value: any, shouldValidate?: boolean) => void;
  setFieldTouched: (
    field: string,
    isTouched?: boolean,
    shouldValidate?: boolean
  ) => void;
  optionsRef?: string;
  deps?: string[];
  dataFetcher?: (
    optionsRef: string
  ) => Promise<AxiosResponse<DataFetcherResponseDto>>;
  isMulti?: boolean;
  isClearable?: boolean;
  hidden?: boolean;
  loadingText?: string;
}

export interface ReactSelectOption {
  value: any;
  label: string;
  description?: string;
}

const toReactSelectOption = (o: SelectOptionDto): ReactSelectOption => ({
  value: o.value,
  label: o.title,
  description: o.description || "",
});

const Option = (props: any) => {
  return (
    <components.Option {...props}>
      <div className="option-wrapper">
        <div className="label">{props.data.label}</div>
        <div className="description">{props.data.description}</div>
      </div>
    </components.Option>
  );
};

const loadingOption = { label: "Loading...", value: "" };
const emptyOption = { label: "", value: "" };

const InputSelect = ({
  title,
  required,
  description,
  options,
  onChange,
  optionsRef,
  deps,
  setFieldValue,
  setFieldTouched,
  dataFetcher,
  values,
  dValues,
  isMulti,
  isClearable,
  ...props
}: InputSelectOptions) => {
  const [field, meta] = useField(props);
  const [optionsDynamic, setOptionsDynamic] = useState(options);
  const [optionsLoading, setOptionsLoading] = useState(false);
  const [key, setKey] = useState<number>(1);
  const depValues = dValues ? dValues : [];
  useEffect(() => {
    if (optionsRef) {
      setOptionsLoading(true);
      dataFetcher?.(optionsRef).then(({ data }) => {
        if (data.options?.length === 1) {
          setFieldValue?.(field.name, data.options[0].value);
        }
        setOptionsDynamic(data.options);
        setOptionsLoading(false);
      });
    } else {
      if (options?.length === 1) {
        setFieldValue?.(field.name, options[0].value);
      }
      setOptionsDynamic(options);
    }
  }, [key, optionsRef, ...depValues]);
  return (
    <div className={props.className}>
      {optionsLoading && props.hidden && (
        <div className="mb-1">
          <Spinner
            as="span"
            animation="border"
            size="sm"
            role="status"
            aria-hidden="true"
          />
          <span className="ml-2">{props.loadingText}</span>
        </div>
      )}
      <div
        className={cn("mb-3", {
          "d-none": props.hidden,
        })}
      >
        {title && (
          <label htmlFor={props.id || props.name} className="form-label">
            {title}
            {required && <span className="required-icon">*</span>}
          </label>
        )}
        <p className="text-muted" style={{ marginBottom: ".5rem" }}>
        {description}
        </p>
        <div className="row position-relative">
          <Select
            {...props}
            options={
              !optionsDynamic
                ? [loadingOption]
                : optionsDynamic.map((option) => toReactSelectOption(option))
            }
            components={{ Option }}
            isMulti={isMulti}
            className="col-12"
            onChange={(v: any | undefined) => {
              let newValue = v?.value;
              if (isMulti) {
                newValue = v?.map((option: any) => option.value);
              }
              setFieldValue?.(field.name, newValue);
            }}
            onBlur={() => setFieldTouched?.(field.name, true)}
            value={getOptionValues(
              field,
              optionsDynamic,
              isMulti,
              optionsLoading
            )}
            isClearable={isClearable}
          />

          {dataFetcher && (
            <div className="select-fetcher">
              <IconRefresh
                size={16}
                role="button"
                onClick={() => setKey(key + 1)}
                className="float-end"
              />
            </div>
          )}
        </div>
        {meta.error ? <div className="error">{meta.error}</div> : null}
      </div>
    </div>
  );
};

function getOptionValues(
  field: FieldInputProps<any>,
  optionsDynamic: SelectOptionDto[] | undefined,
  isMulti?: boolean,
  optionsLoading?: boolean
): ReactSelectOption | ReactSelectOption[] {
  if (optionsLoading || !optionsDynamic) {
    return isMulti ? [] : emptyOption;
  }
  if (!isMulti) {
    const selectedOptions = optionsDynamic
      .filter((o) => ObjectUtils.objectEquals(o.value, field.value))
      .map((o) => toReactSelectOption(o));
    return selectedOptions.length ? selectedOptions[0] : emptyOption;
  } else {
    return optionsDynamic
      .filter((o) => _.includes(field.value, o.value))
      .map((o) => toReactSelectOption(o));
  }
}

export default InputSelect;
