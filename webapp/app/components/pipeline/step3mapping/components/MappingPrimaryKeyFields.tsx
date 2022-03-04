import { Placeholder } from "react-bootstrap";
import Select from "react-select";
import Image from "react-bootstrap/Image";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";
import { useState, useEffect } from "react";
import { addkeysToLocalStorage, defaultValue } from "../utils/MappingAutoFill";
interface MappingPrimaryKeyFieldsProps extends MappingFieldsProps {
  onlyOptions?: SchemaOptions[];
}

export default function MappingPrimaryKeyFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  errors,
}: MappingPrimaryKeyFieldsProps) {
  const [form, setForm] = useState({});

  // On mount check if fields are there in localStorage
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("primaryKeysForm");
    if (getLocalStorageItem) {
      const primaryKeysForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, primaryKeysForm);
    }
  }, []);
  // SECTION - 2 - Primary Keys to match the destination object
  const primaryKeysSection = mappingGroups.filter((fields) => {
    return fields.type === "PRIMARY_KEYS" && fields;
  });

  return (
    <div className="row">
      {primaryKeysSection.length > 0 &&
        primaryKeysSection.map((field) => (
          <>
            <WarehouseColumn
              title={field.title}
              description={field.description}
            >
              <tr>
                <th className="col-6">
                  <Select
                    options={options}
                    onChange={(e) => {
                      setFieldValue?.(
                        `PRIMARY_KEYS-warehouseField-0`,
                        e?.value
                      );
                      addkeysToLocalStorage(
                        e?.value,
                        "primaryKeysForm",
                        "",
                        "warehouseField",
                        ""
                      );
                    }}
                    onBlur={() =>
                      setFieldTouched?.(`PRIMARY_KEYS-warehouseField-0`, true)
                    }
                    placeholder={"Select a column"}
                    defaultValue={
                      defaultValue(
                        "primaryKeysForm",
                        "0",
                        "warehouseField",
                        ""
                      ) && {
                        value: defaultValue(
                          "primaryKeysForm",
                          "0",
                          "warehouseField",
                          ""
                        ),
                        label: defaultValue(
                          "primaryKeysForm",
                          "0",
                          "warehouseField",
                          ""
                        ),
                      }
                    }
                  />
                </th>
                <th>
                  <Image
                    src="/images/arrow-right.svg"
                    alt="Right Arrow for Mapping"
                    className="py-2"
                  />
                </th>
                <th className="col-6">
                  <Select
                    options={field.primaryKeys?.map((key) => ({
                      value: key.fieldName,
                      label: key.fieldDisplayName || key.fieldName,
                    }))}
                    onChange={(e) => {
                      setFieldValue?.(`PRIMARY_KEYS-appField-0`, e?.value);
                      addkeysToLocalStorage(
                        e?.value,
                        "primaryKeysForm",
                        "",
                        "appField",
                        ""
                      );
                    }}
                    onBlur={() =>
                      setFieldTouched?.(`PRIMARY_KEYS-appField-0`, true)
                    }
                    placeholder={"Select a field"}
                    defaultValue={
                      defaultValue("primaryKeysForm", "0", "appField", "") && {
                        value: defaultValue(
                          "primaryKeysForm",
                          "0",
                          "appField",
                          ""
                        ),
                        label: defaultValue(
                          "primaryKeysForm",
                          "0",
                          "appField",
                          ""
                        ),
                      }
                    }
                  />
                </th>
                <Placeholder as="td" className="pb-0">
                  <label className="required-icon">*</label>
                </Placeholder>
              </tr>
            </WarehouseColumn>
            <ErrorMessage errors={errors} include={"rimary"} />
            <hr className="solid" />
          </>
        ))}
    </div>
  );
}
