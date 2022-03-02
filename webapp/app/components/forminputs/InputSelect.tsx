import React, { useEffect, useState } from "react";
import { FieldInputProps, useField } from "formik";
import { InputBaseProps } from "@/app/common/dtos/InputBaseProps";
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import _, { values } from "lodash";
import { AxiosResponse } from "axios";
import { ObjectUtils } from "@/app/common/utils/objectUtils";
import { Spinner } from "react-bootstrap";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import Select from "react-select";
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
}

const toReactSelectOption = (o: SelectOptionDto): ReactSelectOption => ({
  value: o.value,
  label: o.title,
});

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
    <div className="mb-3 d-flex">
      <div className={cn(props.className, "col-12 card p-2")}>
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
          className={cn({
            "d-none": props.hidden,
          })}
        >
          {title && (
            <label htmlFor={props.id || props.name} className="form-label">
              {title}
              {required && <span className="required-icon">*</span>}
            </label>
          )}
          <div className="row">
            <Select
              {...props}
              options={
                !optionsDynamic
                  ? [loadingOption]
                  : optionsDynamic.map((option) => toReactSelectOption(option))
              }
              isMulti={isMulti}
              className={cn({ "border-0": !!dataFetcher, col: !dataFetcher })}
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
          </div>
          {meta.error ? <div className="error">{meta.error}</div> : null}
        </div>
      </div>
      {dataFetcher && (
        <div className={cn("ms-2 my-auto", meta.error ? " pt-4" : " pt-5" )}>
          <IconRefresh
            size={24}
            role="button"
            strokeWidth={1}
            onClick={() => setKey(key + 1)}
          />
        </div>
      )}
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
