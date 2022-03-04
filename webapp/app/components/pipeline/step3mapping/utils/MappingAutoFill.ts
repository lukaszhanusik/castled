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

function defaultValue(
  form: string,
  field: string,
  type: string,
  index: string | number
) {
  const getLocalStorageItem = localStorage.getItem(`${form}`);

  if (getLocalStorageItem) {
    const responseJson = JSON.parse(getLocalStorageItem);
    switch (form) {
      case "importantParamsForm":
        return responseJson[`IMPORTANT_PARAMS-${field}`];
      case "primaryKeysForm":
        return responseJson[`PRIMARY_KEYS-${type}-${field}`];
      case "destinationFieldForm":
        return responseJson[`DESTINATION_FIELDS-${type}-${index}`];
      case "misclFieldForm":
        return responseJson[`MISCELLANEOUS_FIELDS-${type}-${index}`];
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

function addkeysToLocalStorage(
  input: string,
  formType: string,
  field: string,
  type: string,
  index: string | number
) {
  const form = {};

  if (formType === "importantParamsForm") {
    Object.assign(form, {
      [`IMPORTANT_PARAMS-${field}`]: input,
    });
    getAndSetLocalStorage(formType, form);
  }

  if (formType === "primaryKeysForm") {
    Object.assign(form, {
      [`PRIMARY_KEYS-${type}-0`]: input,
    });
    getAndSetLocalStorage(formType, form);
  }

  if (formType === "destinationFieldForm") {
    Object.assign(form, {
      [`DESTINATION_FIELDS-${type}-${index}`]: input,
    });
    getAndSetLocalStorage(formType, form);
  }

  if (formType === "misclFieldForm") {
    Object.assign(form, {
      [`MISCELLANEOUS_FIELDS-${type}-${index}`]: input,
    });
    getAndSetLocalStorage(formType, form);
  }
}

export {
  removeAllLocalStorageMapping,
  deleteItemFromLocalStorage,
  defaultValue,
  addkeysToLocalStorage,
};
