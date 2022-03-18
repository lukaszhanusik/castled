package io.castled.apps.connectors.Iterable;

import io.castled.apps.connectors.Iterable.client.dtos.CatalogItemField;
import io.castled.apps.connectors.Iterable.client.dtos.EventField;
import io.castled.apps.connectors.Iterable.client.dtos.IterableFieldType;
import io.castled.apps.connectors.Iterable.client.dtos.UserPrimaryKey;
import io.castled.apps.models.GenericSyncObject;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.SchemaConstants;
import io.castled.schema.models.RecordSchema;
import io.castled.schema.models.Schema;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IterableSchemaUtils {

    private static final Map<String, IterableFieldType> iterableFieldTypeMap = Arrays.stream(IterableFieldType.values())
            .collect(Collectors.toMap(fieldType -> fieldType.getType(), Function.identity()));

    private static final Map<String, UserPrimaryKey> iterableUserPrimaryKeyMap = Arrays.stream(UserPrimaryKey.values())
            .collect(Collectors.toMap(pKey -> pKey.getName(), Function.identity()));

    private static final Map<String, EventField> iterableEventFieldMap = Arrays.stream(EventField.values())
            .collect(Collectors.toMap(eventField -> eventField.getName(), Function.identity()));


    public static RecordSchema getSchema(IterableSyncConfig iterableSyncConfig, Map<String, String> fields) {
        IterableObject iterableObject = Arrays.stream(IterableObject.values()).filter(obj ->
                obj.getName().equals(iterableSyncConfig.getObject().getObjectName())).findFirst().get();

        RecordSchema.Builder schemaBuilder = RecordSchema.builder().name(iterableObject.getName());

        // Get all fields except objects.
        fields.entrySet().stream().filter(entry -> iterableFieldTypeMap.get(entry.getValue()) != IterableFieldType.OBJECT)
                .forEach(entry -> schemaBuilder.put(entry.getKey(), getInternalSchema(iterableFieldTypeMap.get(entry.getValue()))));
        return schemaBuilder.build();
    }

    public static boolean isUserPrimaryKey(String fieldName) {
        return iterableUserPrimaryKeyMap.containsKey(fieldName);
    }

    public static boolean isCatalogPrimaryKey(String fieldName) {
        return CatalogItemField.ITEM_ID.getName().equals(fieldName);
    }

    public static boolean isCustomEventField(String fieldName) {
        return !iterableEventFieldMap.containsKey(fieldName);
    }

    public static IterableObject getIterableObject(GenericSyncObject genericObj) {
        switch (genericObj.getObjectName()) {
            case "users":
                return IterableObject.USERS;
            case "events":
                return IterableObject.EVENTS;
            case "catalogs":
                return IterableObject.CATALOGS;
            default:
                throw new CastledRuntimeException("Invalid object type!");
        }
    }

    private static Schema getInternalSchema(IterableFieldType fieldType) {
        switch (fieldType) {
            case STRING:
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case LONG:
            case INTEGER:
                return SchemaConstants.OPTIONAL_LONG_SCHEMA;
            case DATE:
                return SchemaConstants.OPTIONAL_DATE_SCHEMA;
            case BOOLEAN:
                return SchemaConstants.OPTIONAL_BOOL_SCHEMA;
            default:
                throw new CastledRuntimeException(String.format("Iterable invalid type: %s", fieldType));
        }
    }
}
