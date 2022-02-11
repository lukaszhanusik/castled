import React, { useEffect, useState } from "react";
import { useField } from "formik";
import { InputBaseProps } from "@/app/common/dtos/InputBaseProps";
import { SelectOptionDto } from "@/app/common/dtos/SelectOptionDto";
import _, { values } from "lodash";
import { AxiosResponse } from "axios";
import { ObjectUtils } from "@/app/common/utils/objectUtils";
import RadioGroup from "react-native-radio-buttons-group";
import { Spinner } from "react-bootstrap";
import { DataFetcherResponseDto } from "@/app/common/dtos/DataFetcherResponseDto";
import Select from "react-select";
import cn from "classnames";
import { IconRefresh } from "@tabler/icons";
import { stringify } from "querystring";

export interface InputRadioButtonOptions extends InputBaseProps {
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

const InputRadioButton = ({
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
}: InputRadioButtonOptions) => {
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

  console.log("--field.value--");

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
          {optionsDynamic &&
            optionsDynamic.map((item: any, index: number) => (
              <>
                <label htmlFor={item.title + index}>
                  {" "}
                  <input
                    name={props.id || props.name}
                    key={item.title + index}
                    id={item.title + index}
                    // value={item.value}
                    type="radio"
                    // onChange={(v: any) => setFieldValue?.(field.name, v?.value)}
                    onChange={(v: any) => handleRegisterUser(field.name, v?.value)}
                    onClick={() => setFieldTouched?.(field.name, true)}
                  />
                  &nbsp;&nbsp; {item.title}
                </label>
              </>
            ))}
        </div>
        {meta.error ? <div className="error">{meta.error}</div> : null}
      </div>

      <div>-------------------- to remove -------------</div>
    </div>
  );
};

const handleRegisterUser =  (field:any, isValid:any) => {
  console.log()
  // setFieldTouched?.(field.name, true)
};

export default InputRadioButton;
