import React from "react";
import TextareaAutosize from "react-textarea-autosize";
import { FieldMapping } from "@/app/common/dtos/PipelineCreateRequestDto";
import { IconCheck } from "@tabler/icons";
import classNames from "classnames";
import { Col, Row, Table } from "react-bootstrap";

export interface PipelineMappingViewRestApiProps {
  sourceQuery: string | undefined;
  dataMapping:
    | {
        headers: any | undefined;
        method: string | undefined;
        primaryKeys: string[];
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
  if (dataMapping && dataMapping.headers && dataMapping.headers) {
    for (let key in dataMapping.headers) {
      headers.push({ key: key, value: dataMapping.headers[key] });
    }
  }

  return (
    <>
      <h3 className="mb-1 mt-3 font-weight-bold">SQL Query</h3>
      <TextareaAutosize
        minRows={3}
        maxRows={25}
        value={sourceQuery}
        disabled={true}
        className="w-100"
      />
      <hr />

      <h4 className="mb-1 mt-4 font-weight-bold">
        Primary Keys :
        {dataMapping !== undefined &&
          dataMapping.primaryKeys &&
          dataMapping.primaryKeys.map((key, i) => <i>{key}</i>)}
      </h4>
      <h4 className="mb-1 mt-4 font-weight-bold">Mapping</h4>
      {dataMapping !== undefined && dataMapping.template && (
        <TextareaAutosize
          className="w-100"
          defaultValue={dataMapping.template}
          disabled={true}
        />
        // <textarea value={dataMapping.template} />
      )}

      <h4 className="mb-1 mt-4 font-weight-bold">Headers</h4>

      <Table hover>
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
    </>
  );
};

export default PipelineMappingViewRestApi;
