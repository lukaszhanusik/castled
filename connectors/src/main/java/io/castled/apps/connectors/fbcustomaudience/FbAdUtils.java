package io.castled.apps.connectors.fbcustomaudience;

import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbAudienceUserFields;
import io.castled.schema.SchemaConstants;
import io.castled.schema.models.RecordSchema;

import java.util.Arrays;

public class FbAdUtils {

    public static RecordSchema getSchema() {
        final String OBJECT_NAME = "Custom Audiences";
        RecordSchema.Builder schemaBuilder = RecordSchema.builder().name(OBJECT_NAME);
        // All field treated as strings
        Arrays.stream(FbAudienceUserFields.values())
                .forEach(enumVal -> schemaBuilder.put(enumVal.getName(), SchemaConstants.OPTIONAL_STRING_SCHEMA));
        return schemaBuilder.build();
    }
}
