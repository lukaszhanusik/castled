import React from "react";
import TextareaAutosize from "react-textarea-autosize";
import { FieldMapping } from "@/app/common/dtos/PipelineCreateRequestDto";
import { Col, Row, Table } from "react-bootstrap";
import CodeInput from "../forminputs/CodeInput";

export interface PipelineMappingViewRestApiProps {
  sourceQuery: string | undefined;
  dataMapping:
    | {
        headers: any | undefined;
        method: string | undefined;
        template: string | undefined;
        url: string | undefined;
        fieldMappings: FieldMapping[];
      }
    | undefined;
}

const PipelineMappingViewRestApi = ({
  sourceQuery,
  dataMapping,
}: PipelineMappingViewRestApiProps) => {
  let headers = [];
  if (dataMapping && dataMapping.headers) {
    for (let key in dataMapping.headers) {
      headers.push({ key: key, value: dataMapping.headers[key] });
    }
  }

  return (
    <>
      <h3 className="mb-1 mt-3 font-weight-bold">SQL Query</h3>
      <CodeInput value={sourceQuery} editable={false} minHeight={"50px"} />

      <label className="form-label mt-3 mb-0">URL</label>
      <input
        className="form-control"
        value={dataMapping?.url}
        disabled={true}
      />

      <label className="form-label mt-3 mb-0">HTTP Method</label>
      <input
        className="form-control"
        value={dataMapping?.method}
        disabled={true}
      />

      <h4 className="mb-0 mt-3 font-weight-bold">Headers</h4>
      <Table>
        <thead>
          <tr>
            <th>Key</th>
            <th>Value</th>
          </tr>
        </thead>
        <tbody>
          {headers &&
            headers.map((header, i) => (
              <tr key={i}>
                <td>{header.key}</td>
                <td>{header.value}</td>
              </tr>
            ))}
        </tbody>
      </Table>

      <h4 className="mb-0 mt-3 font-weight-bold">Column Mapping</h4>
      {dataMapping !== undefined && dataMapping.template && (
        <TextareaAutosize
          className="w-100"
          defaultValue={dataMapping.template}
          disabled={true}
        />
      )}
    </>
  );
};

export default PipelineMappingViewRestApi;
