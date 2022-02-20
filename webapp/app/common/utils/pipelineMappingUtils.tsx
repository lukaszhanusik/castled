import { ConnectorSchema, PipelineSchemaResponseDto } from "../dtos/PipelineSchemaResponseDto";
import { SelectOptionDto } from "../dtos/SelectOptionDto";

const getAppSchemaFields = (
  connectorSchema: ConnectorSchema | undefined
): SelectOptionDto[] | undefined =>
  connectorSchema?.fields.map((field) => ({
    value: field.fieldName,
    title: field.fieldName,
  }));

export default { getSchemaFieldsAsOptions: getAppSchemaFields };
