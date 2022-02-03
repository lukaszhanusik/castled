import { StringAnyMap } from "./types";
import {
  FormFieldSchema,
  FormFieldMeta,
  FormFieldsDto,
  GroupActivator,
} from "./../dtos/FormFieldsDto";
import _ from "lodash";
import * as yup from "yup";
import jexl from "jexl";
import jsonUtils from "./jsonUtils";

const isFieldHidden = (
  groupActivator: GroupActivator,
  namePrefix: string | undefined,
  values: any,
  depValues: any[]
): boolean => {
  let skip = false;
  if (!groupActivator) return skip;
  for (const dependency of groupActivator.dependencies) {
    const dependencyName = namePrefix
      ? `${namePrefix}.${dependency}`
      : dependency;
    const depValue: any = _.get(values, dependencyName);
    if (!depValue) {
      skip = true;
      break;
    }
    depValues.push(JSON.stringify(depValue));
  }
  if (groupActivator?.condition) {
    skip = !jexl.evalSync(groupActivator.condition!, values);
  }
  return skip;
};

const emptyStringToNull = (value: any, originalValue: any): any => {
  if (typeof originalValue === "string" && originalValue === "") {
    return null;
  }
  return value;
};

const getFieldValidator = (field: FormFieldMeta) => {
  const { validations, errorMessages } = field;
  let { schema } = field;
  const { type } = field.fieldProps;
  let fieldValidator: yup.AnySchema<any> | null = null;
  if (!schema) {
    // Set default schema
    if (type === "TEXT_BOX" || type === "RADIO_GROUP" || type === "DROP_DOWN") {
      schema = FormFieldSchema.STRING;
    } else if (type === "CHECK_BOX") {
      schema = FormFieldSchema.BOOLEAN;
    }
  }
  if (schema === FormFieldSchema.NUMBER) {
    let validator: yup.NumberSchema<any> = yup
      .number()
      .typeError("Must be a number")
      .transform(emptyStringToNull)
      .nullable();
    if (validations.required) {
      validator = validator.required(errorMessages?.required || `Required`);
    }
    if (validations.min) {
      validator = validator.min(
        validations.min,
        errorMessages?.min || `Cannot be less than ${errorMessages?.min}`
      );
    }
    if (validations.max) {
      validator = validator.max(
        validations.max,
        errorMessages?.max || `Cannot be less than ${errorMessages?.min}`
      );
    }
    fieldValidator = validator;
  } else if (schema === FormFieldSchema.OBJECT) {
    let validator: yup.ObjectSchema<any> = yup.object();
    if (validations.required) {
      validator = validator.required(errorMessages?.required || `Required`);
    }
    fieldValidator = validator;
  }
  if (schema === FormFieldSchema.ARRAY) {
    let validator: yup.ArraySchema<any> = yup.array();
    if (validations.required) {
      validator = validator.required(errorMessages?.required || `Required`);
    }
    fieldValidator = validator;
  } else if (
    schema === FormFieldSchema.STRING ||
    schema === FormFieldSchema.DATE ||
    schema === FormFieldSchema.ENUM
  ) {
    let validator: yup.StringSchema<any> = yup.string();
    if (validations.required) {
      validator = validator.required(errorMessages?.required || `Required`);
    }
    if (validations.min) {
      validator = validator.min(
        validations.min,
        errorMessages?.min ||
          `Cannot be less than ${errorMessages?.min} characters in length`
      );
    }
    if (validations.max) {
      validator = validator.max(
        validations.max,
        errorMessages?.max ||
          `Cannot be less than ${errorMessages?.min} characters in length`
      );
    }
    if (validations.regex) {
      validator = validator.matches(
        new RegExp(validations.regex),
        errorMessages?.regex ||
          `Should match the pattern ${errorMessages?.regex}`
      );
    }
    fieldValidator = validator;
  }
  return fieldValidator;
};

const getValidationSchema = (
  formFields: FormFieldsDto | undefined,
  namePrefix: string | undefined,
  values: any,
  moreValidations: StringAnyMap
) => {
  if (!formFields) return yup.object().shape(moreValidations);
  const configShape: StringAnyMap = {};
  let shape: StringAnyMap = {};
  _.map(formFields.fields, (field, key) => {
    const groupActivator = formFields.groupActivators[field.group];
    const depValues: any[] = [];
    let skip = isFieldHidden(groupActivator, namePrefix, values, depValues);
    if (skip) return true;
    const fieldValidator = getFieldValidator(field);
    if (fieldValidator) {
      configShape[key] = fieldValidator;
    }
  });
  if (namePrefix) {
    shape[namePrefix] = yup.object().shape(configShape);
  } else {
    shape = configShape;
  }
  return yup.object().shape(_.merge(shape, moreValidations));
};

const getValidationErrors = (
  formFields: FormFieldsDto | undefined,
  namePrefix: string | undefined,
  values: any,
  moreValidations: StringAnyMap
): StringAnyMap => {
  const schema = getValidationSchema(
    formFields,
    namePrefix,
    values,
    moreValidations
  );
  if (!schema) return {};
  let errors: StringAnyMap = {};
  try {
    schema.validateSync(values, { abortEarly: false });
  } catch (e) {
    const errorMeta: any = jsonUtils.safeJSON(e);
    for (var meta of errorMeta.inner) {
      _.set(errors, meta.path, meta.message);
    }
  }
  return errors;
};

const getInitialValues = (
  formFields: FormFieldsDto | undefined,
  namePrefix: string | undefined,
  moreInitialValues?: StringAnyMap
): any => {
  const initialValues = {};
  if (!formFields) return moreInitialValues;
  _.map(formFields.fields, (field, key) => {
    const fullKey = (namePrefix ? `${namePrefix}.` : "") + key;
    _.set(initialValues, fullKey, undefined);
  });
  return _.merge(initialValues, moreInitialValues);
};

export default {
  isFieldHidden,
  getValidationSchema,
  getValidationErrors,
  getInitialValues,
};
