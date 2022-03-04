function removeAllLocalStorageMapping() {
  localStorage.removeItem("importantParamsForm");
  localStorage.removeItem("primaryKeysForm");
  localStorage.removeItem("destinationFieldForm");
  localStorage.removeItem("misclFieldForm");
}

function deleteItemFromLocalStorage(key: string, type: string) {
  switch (type) {
    case "destinationFieldForm":
      const getDestinationLocalStorageItem = localStorage.getItem(
        "destinationFieldForm"
      );
      if (getDestinationLocalStorageItem) {
        const parseStorageItems = JSON.parse(getDestinationLocalStorageItem);
        delete parseStorageItems[
          `DESTINATION_FIELDS-optional-warehouseField-${key}`
        ];
        delete parseStorageItems[`DESTINATION_FIELDS-optional-appField-${key}`];
        localStorage.setItem(
          "destinationFieldForm",
          JSON.stringify(parseStorageItems)
        );
      }
    case "misclFieldForm":
      const getMisclLocalStorageItem = localStorage.getItem("misclFieldForm");
      if (getMisclLocalStorageItem) {
        const parseStorageItems = JSON.parse(getMisclLocalStorageItem);
        delete parseStorageItems[`MISCELLANEOUS_FIELDS-warehouseField-${key}`];
        delete parseStorageItems[`MISCELLANEOUS_FIELDS-appField-${key}`];
        localStorage.setItem(
          "misclFieldForm",
          JSON.stringify(parseStorageItems)
        );
      }
  }
}

interface IDefaultValueProps {
  form: string;
  type?: string;
  index?: number | string;
  field?: string;
}

function defaultValue({ form, field, type, index }: IDefaultValueProps): string {

  const getLocalStorageItem = localStorage.getItem(`${form}`);
  
  switch (form) {
    case "destinationFieldForm":
      if (getLocalStorageItem) {
        const primaryKeysForm = JSON.parse(getLocalStorageItem);
        return primaryKeysForm[`DESTINATION_FIELDS-${type}-${index}`];
      }

    case "primaryKeysForm":
      const getLocalStorageItem = localStorage.getItem("primaryKeysForm");
      if (getLocalStorageItem) {
        const primaryKeysForm = JSON.parse(getLocalStorageItem);
        return primaryKeysForm[`PRIMARY_KEYS-${type}-${field}`];
      }


  

  function defaultValue(field: string) {
    const getLocalStorageItem = localStorage.getItem("importantParamsForm");
    if (getLocalStorageItem) {
      const importantParamsForm = JSON.parse(getLocalStorageItem);
      return importantParamsForm[`IMPORTANT_PARAMS-${field}`];
    }
  }

  function defaultValue(type: string, index: number | string) {
    const getLocalStorageItem = localStorage.getItem("misclFieldForm");
    if (getLocalStorageItem) {
      const primaryKeysForm = JSON.parse(getLocalStorageItem);
      return primaryKeysForm[`MISCELLANEOUS_FIELDS-${type}-${index}`];
    }
  }
}
export { removeAllLocalStorageMapping, deleteItemFromLocalStorage };
