package io.castled.apps.connectors.fbconversion;

import io.castled.apps.connectors.fbconversion.client.dtos.CustomDataField;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomerInfoField;
import io.castled.apps.connectors.fbconversion.client.dtos.ServerEventField;
import io.castled.schema.SchemaConstants;
import io.castled.schema.models.RecordSchema;
import io.castled.schema.models.Schema;

import java.util.Arrays;

public class FbConversionSchemaUtils {

    public static RecordSchema getSchema() {
        final String OBJECT_NAME = "Conversion Events";
        RecordSchema.Builder schemaBuilder = RecordSchema.builder().name(OBJECT_NAME);

        // Server event params
        Arrays.stream(ServerEventField.values())
                .forEach(enumVal -> schemaBuilder.put(enumVal.getName(), getInternalSchema(enumVal)));

        // Customer info params
        Arrays.stream(CustomerInfoField.values())
                .forEach(enumVal -> schemaBuilder.put(enumVal.getName(), getInternalSchema(enumVal)));

        // Custom data params
        Arrays.stream(CustomDataField.values())
                .forEach(enumVal -> schemaBuilder.put(enumVal.getName(), getInternalSchema(enumVal)));

        return schemaBuilder.build();
    }

    private static Schema getInternalSchema(ServerEventField paramsEnum) {
        switch (paramsEnum) {
            case DATA_PROC_OPT_CT:
            case DATA_PROC_OPT_ST:
                return SchemaConstants.OPTIONAL_LONG_SCHEMA;
            case OPT_OUT:
                return SchemaConstants.OPTIONAL_BOOL_SCHEMA;
            default:
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
        }
    }

    private static Schema getInternalSchema(CustomerInfoField paramsEnum) {
        switch (paramsEnum) {
            case DB:
                return SchemaConstants.OPTIONAL_DATE_SCHEMA;
            case LEAD_ID:
            case LOGIN_ID:
                return SchemaConstants.OPTIONAL_LONG_SCHEMA;
            default:
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
        }
    }

    private static Schema getInternalSchema(CustomDataField paramsEnum) {
        switch (paramsEnum) {
            case VALUE:
            case PREDICTED_LTV:
                return SchemaConstants.OPTIONAL_DOUBLE_SCHEMA;
            default:
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
        }
    }
}