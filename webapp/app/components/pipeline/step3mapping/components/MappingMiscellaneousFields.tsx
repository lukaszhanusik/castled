import { MappingGroup } from "@/app/common/dtos/PipelineSchemaResponseDto";
import { MappingFieldsProps } from "../types/componentTypes";
import AdditionalFields from "./Layouts/AdditionalFields";
import WarehouseColumn from "./Layouts/WarehouseColumn";

interface MappingMiscellaneousFieldsProps extends MappingFieldsProps {
  field: MappingGroup;
}

export default function MappingMiscellaneousFields({
  options,
  mappingGroups,
  values,
  setFieldValue,
  setFieldTouched,
  // field,
}: MappingFieldsProps) {
  // SECTION - 4 - Miscellaneous fields filter from warehouseSchema
  const miscellaneousFieldSection = mappingGroups.filter((fields) => {
    return fields.type === "MISCELLANEOUS_FIELDS" && fields;
  });

  return (
    <div className="row py-2">
      {miscellaneousFieldSection.length > 0 &&
        miscellaneousFieldSection?.map((field) => (
          <WarehouseColumn title={field.title} description={field.description}>
            <AdditionalFields
              options={options}
              mappingGroups={mappingGroups}
              type="input"
              setFieldValue={setFieldValue}
              setFieldTouched={setFieldTouched}
              // field={field}
            />
          </WarehouseColumn>
        ))}
    </div>
  );
}
