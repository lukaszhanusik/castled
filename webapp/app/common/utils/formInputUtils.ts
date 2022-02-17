import { SelectOptionDto } from "./../dtos/SelectOptionDto";
import { StringAnyMap } from "./types";
import _ from "lodash";
import { cpuUsage } from "process";

export interface ReactSelectOption {
  value: any;
  label: string;
}

const getEnumSelectOptions = (titleMap: StringAnyMap) => {
  const options: SelectOptionDto[] = [];
  _.map(titleMap, (title, key) => {
    options.push({
      title,
      value: key,
    });
  });
  return options;
};

const insertTextInInput = (
  ref: any,
  userInput: string,
  oldValue: string,
  setValue: React.Dispatch<React.SetStateAction<string>>
) => {
  const selectionStart = ref && ref.current ? ref.current.selectionStart : 0;
  const selectionEnd = ref && ref.current ? ref.current.selectionEnd : 0;

  let newValue =
    oldValue.substring(0, selectionStart) +
    userInput +
    oldValue.substring(selectionEnd, oldValue.length);
  console.log(newValue);
  setValue(newValue);
};

export default { getEnumSelectOptions, insertTextInInput };
