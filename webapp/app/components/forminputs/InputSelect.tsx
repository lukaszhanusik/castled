import React, { useEffect, useState } from "react";
import { useField } from "formik";
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
  hidden?: boolean;
  loadingText?: string;
}

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
            {required && "*"}
            {title}
          </label>
        )}
        <div className="row">
          <Select
            {...props}
            options={
              !optionsDynamic
                ? [{ label: "Loading.." }]
                : optionsDynamic.map((o) => ({
                    value: o.value,
                    label: o.title,
                  }))
            }
            isClearable
            className={cn({ "col-11": !!dataFetcher, col: !dataFetcher })}
            onChange={(v) => setFieldValue?.(field.name, v?.value)}
            onBlur={() => setFieldTouched?.(field.name, true)}
            value={
              optionsLoading || !optionsDynamic
                ? { label: "Loading..." }
                : {
                    value: field.value,
                    label: optionsDynamic
                      .filter((o) =>
                        ObjectUtils.objectEquals(o.value, field.value)
                      )
                      .map((o) => o.title),
                  }
            }
          />
          {dataFetcher && (
            <div className="col-1 my-auto">
              <IconRefresh
                size={24}
                role="button"
                onClick={() => setKey(key + 1)}
              />
            </div>
          )}
        </div>
        {meta.error ? <div className="error">{meta.error}</div> : null}
      </div>
    </div>
  );
};
export default InputSelect;
