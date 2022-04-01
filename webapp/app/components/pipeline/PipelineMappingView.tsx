import React from "react";

import TextareaAutosize from "react-textarea-autosize";
import { Card, Table } from "react-bootstrap";
import { FieldMapping } from "@/app/common/dtos/PipelineCreateRequestDto";
import { IconCheck } from "@tabler/icons";
import classNames from "classnames";
import CodeInput from "../forminputs/CodeInput";

export interface PipelineMappingViewProps {
  sourceQuery: string | undefined;
  dataMapping:
    | {
        primaryKeys: string[];
        fieldMappings: FieldMapping[];
      }
    | undefined;
}

const PipelineMappingView = ({
  sourceQuery,
  dataMapping,
}: PipelineMappingViewProps) => {
  return (
    <>
      <h3 className="mb-1 mt-3 font-weight-bold">SQL Query</h3>
      <CodeInput value={sourceQuery} editable={false} minHeight={"50px"} />
      <h3 className="mb-1 mt-4 font-weight-bold">Mapping</h3>
      <Table>
        <thead>
          <tr>
            <th>Warehouse Field</th>
            <th>App Field</th>
            <th>Primary key</th>
          </tr>
        </thead>
        <tbody>
          {dataMapping !== undefined &&
            dataMapping.fieldMappings &&
            dataMapping.fieldMappings.map((fieldMapping, i) => (
              <tr key={i}>
                <td>{fieldMapping.warehouseField}</td>
                <td>{fieldMapping.appField}</td>
                <td>
                  <IconCheck
                    className={classNames({
                      "d-none": !dataMapping.primaryKeys.includes(
                        fieldMapping.appField!
                      ),
                    })}
                  />
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
    </>
  );
};

export default PipelineMappingView;
