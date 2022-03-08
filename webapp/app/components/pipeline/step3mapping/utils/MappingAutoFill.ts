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
  field?: string;
  type?: string;
  index?: string | number;
}

function defaultValue(context: IDefaultValueProps) {
  const getLocalStorageItem = localStorage.getItem(`${context.form}`);

  if (getLocalStorageItem) {
    const responseJson = JSON.parse(getLocalStorageItem);
    switch (context.form) {
      case "importantParamsForm":
        return responseJson[`IMPORTANT_PARAMS-${context.field}`];
      case "primaryKeysForm":
        return responseJson[`PRIMARY_KEYS-${context.type}-${context.field}`];
      case "destinationFieldForm":
        return responseJson[
          `DESTINATION_FIELDS-${context.type}-${context.index}`
        ];
      case "misclFieldForm":
        return responseJson[
          `MISCELLANEOUS_FIELDS-${context.type}-${context.index}`
        ];
    }
  }
}

function getAndSetLocalStorage(
  formType: string,
  form: { [key: string]: string }
) {
  const getLocalStorageItem = localStorage.getItem(formType);
  const combineAllItems = getLocalStorageItem
    ? Object.assign(JSON.parse(getLocalStorageItem), form)
    : form;
  localStorage.setItem(formType, JSON.stringify(combineAllItems));
}

interface IAddKeysToLocalStorageProps {
  input: string;
  formType: string;
  field?: string;
  type?: string;
  index?: string | number;
}

function addkeysToLocalStorage(context: IAddKeysToLocalStorageProps) {
  const form = {};

  if (context.formType === "importantParamsForm") {
    Object.assign(form, {
      [`IMPORTANT_PARAMS-${context.field}`]: context.input,
    });
    getAndSetLocalStorage(context.formType, form);
  }

  if (context.formType === "primaryKeysForm") {
    Object.assign(form, {
      [`PRIMARY_KEYS-${context.type}-0`]: context.input,
    });
    getAndSetLocalStorage(context.formType, form);
  }

  if (context.formType === "destinationFieldForm") {
    Object.assign(form, {
      [`DESTINATION_FIELDS-${context.type}-${context.index}`]: context.input,
    });
    getAndSetLocalStorage(context.formType, form);
  }

  if (context.formType === "misclFieldForm") {
    Object.assign(form, {
      [`MISCELLANEOUS_FIELDS-${context.type}-${context.index}`]: context.input,
    });
    getAndSetLocalStorage(context.formType, form);
  }
}

function formatLabel(label: string) {
  return label
    .split("_")
    .join(" ");
    // .map((word) => word[0].toUpperCase() + word.slice(1))
}

export {
  removeAllLocalStorageMapping,
  deleteItemFromLocalStorage,
  defaultValue,
  addkeysToLocalStorage,
  formatLabel,
};
