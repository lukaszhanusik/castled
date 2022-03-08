import { Placeholder } from "react-bootstrap";
import Select from "react-select";
import Image from "react-bootstrap/Image";
import { MappingFieldsProps, SchemaOptions } from "../types/componentTypes";
import ErrorMessage from "./Layouts/ErrorMessage";
import WarehouseColumn from "./Layouts/WarehouseColumn";
import { useEffect } from "react";
import {
  addkeysToLocalStorage,
  defaultValue,
  formatLabel,
} from "../utils/MappingAutoFill";
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
  // On mount check if fields are there in localStorage
  useEffect(() => {
    const getLocalStorageItem = localStorage.getItem("primaryKeysForm");
    if (getLocalStorageItem) {
      const primaryKeysForm = JSON.parse(getLocalStorageItem);
      Object.assign(values, primaryKeysForm);
    }
  }, []);

  return (
    <div className="row">
      {mappingGroups && (
        <>
          <WarehouseColumn
            title={mappingGroups.title}
            description={mappingGroups.description}
          >
            <tr>
              <th className="col-6">
                <Select
                  options={options}
                  onChange={(e) => {
                    setFieldValue?.(`PRIMARY_KEYS-warehouseField-0`, e?.value);
                    addkeysToLocalStorage({
                      input: e?.value,
                      formType: "primaryKeysForm",
                      type: "warehouseField",
                    });
                  }}
                  onBlur={() =>
                    setFieldTouched?.(`PRIMARY_KEYS-warehouseField-0`, true)
                  }
                  placeholder={"Select a column"}
                  defaultValue={
                    defaultValue({
                      form: "primaryKeysForm",
                      field: "0",
                      type: "warehouseField",
                    }) && {
                      value: defaultValue({
                        form: "primaryKeysForm",
                        field: "0",
                        type: "warehouseField",
                      }),
                      label: formatLabel(
                        defaultValue({
                          form: "primaryKeysForm",
                          field: "0",
                          type: "warehouseField",
                        })
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
                  options={mappingGroups.primaryKeys?.map((key) => ({
                    value: key.fieldName,
                    label: key.fieldDisplayName || key.fieldName,
                  }))}
                  onChange={(e) => {
                    setFieldValue?.(`PRIMARY_KEYS-appField-0`, e?.value);
                    addkeysToLocalStorage({
                      input: e?.value,
                      formType: "primaryKeysForm",
                      type: "appField",
                    });
                  }}
                  onBlur={() =>
                    setFieldTouched?.(`PRIMARY_KEYS-appField-0`, true)
                  }
                  placeholder={"Select a field"}
                  defaultValue={
                    defaultValue({
                      form: "primaryKeysForm",
                      field: "0",
                      type: "appField",
                    }) && {
                      value: defaultValue({
                        form: "primaryKeysForm",
                        field: "0",
                        type: "appField",
                      }),
                      label: formatLabel(
                        defaultValue({
                          form: "primaryKeysForm",
                          field: "0",
                          type: "appField",
                        })
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
      )}
    </div>
  );
}
