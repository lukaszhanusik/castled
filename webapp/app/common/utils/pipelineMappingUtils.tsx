import { ConnectorSchema } from "../dtos/PipelineSchemaResponseDto2";
import { SelectOptionDto } from "../dtos/SelectOptionDto";

const getAppSchemaFields = (
  connectorSchema: ConnectorSchema | undefined
): SelectOptionDto[] | undefined =>
  connectorSchema?.fields.map((field: { fieldName: any }) => ({
    value: field.fieldName,
    title: field.fieldName,
  }));

export default { getSchemaFieldsAsOptions: getAppSchemaFields };
