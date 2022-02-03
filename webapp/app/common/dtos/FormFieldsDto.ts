import { FormFieldType } from "../enums/FormFieldType";
import { CodeBlock } from "./CodeBlock";
import { HelpText } from "./HelpText";

export interface FormFieldsDto {
  fields: {
    [key: string]: FormFieldMeta;
  };
  helpText?: HelpText;
  codeBlock?: CodeBlock;
  groupActivators: {
    [key: string]: GroupActivator;
  };
}

export interface GroupActivator {
  dependencies: string[];
  condition?: string;
}

export enum FormFieldSchema {
  STRING = "STRING",
  NUMBER = "NUMBER",
  DATE = "DATE",
  BOOLEAN = "BOOLEAN",
  ENUM = "ENUM",
  OBJECT = "OBJECT",
  ARRAY = "ARRAY",
}

export interface FormFieldValidation {
  required: boolean;
  min: number;
  max: number;
  regex: string;
}

export interface FormFieldMeta {
  group: string;
  fieldProps: {
    type: FormFieldType;
    title: string;
    description: string;
    placeholder?: string;
    optionsRef?: string;
  };
  validations: FormFieldValidation;
  schema: FormFieldSchema;
  errorMessages?: {
    required: string;
    min: string;
    max: string;
    regex: string;
  };
}
