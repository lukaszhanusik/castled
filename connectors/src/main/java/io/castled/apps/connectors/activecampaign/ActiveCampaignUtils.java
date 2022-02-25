package io.castled.apps.connectors.activecampaign;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.castled.apps.connectors.activecampaign.constant.ActiveCampaignConstants;
import io.castled.apps.connectors.activecampaign.dto.CustomDataAttribute;
import io.castled.models.AppFieldDetails;
import io.castled.schema.SchemaConstants;
import io.castled.schema.models.RecordSchema;
import io.castled.schema.models.Schema;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ActiveCampaignUtils {

    public static RecordSchema getSchema(ActiveCampaignObject activeCampaignObject, List<CustomDataAttribute> dataAttributes) {
        RecordSchema.Builder customerSchemaBuilder = RecordSchema.builder().name(activeCampaignObject.getName());

        for (ActiveCampaignObjectFields.CONTACTS_FIELDS field : ActiveCampaignObjectFields.CONTACTS_FIELDS.values()) {
            customerSchemaBuilder.put(field.getFieldName(), field.getSchema(),
                    ImmutableMap.of(ActiveCampaignConstants.CUSTOM_FIELD_ID, field.getFieldTitle(), ActiveCampaignConstants.CUSTOM_FIELD_INDICATOR, false));
        }

        for (CustomDataAttribute dataAttribute : dataAttributes) {
            customerSchemaBuilder.put(dataAttribute.getTitle(), Optional.ofNullable(getFieldSchema(activeCampaignObject, dataAttribute)).orElse(null),
                    ImmutableMap.of(ActiveCampaignConstants.CUSTOM_FIELD_ID, dataAttribute.getId(), ActiveCampaignConstants.CUSTOM_FIELD_INDICATOR, true));
        }
        return customerSchemaBuilder.build();
    }


    public static List<AppFieldDetails> getAppFieldDetails(ActiveCampaignObject activeCampaignObject, List<CustomDataAttribute> dataAttributes) {

        List<AppFieldDetails> appFieldDetails = Lists.newArrayList();
        for (ActiveCampaignObjectFields.CONTACTS_FIELDS field : ActiveCampaignObjectFields.CONTACTS_FIELDS.values()) {
            if (!ActiveCampaignObjectFields.CONTACTS_FIELDS.EMAIL.equals(field)) {
                appFieldDetails.add(AppFieldDetails.builder()
                        .internalName(field.getFieldName())
                        .displayName(field.getFieldTitle())
                        .optional(true)
                        .custom(false)
                        .build());
            }
        }

        for (CustomDataAttribute dataAttribute : dataAttributes) {
            appFieldDetails.add(AppFieldDetails.builder()
                    .internalName(dataAttribute.getTitle())
                    .displayName(dataAttribute.getTitle())
                    .optional(true)
                    .custom(true)
                    .build());
        }
        return appFieldDetails;
    }

    public static String getFieldSchemaType(ActiveCampaignObject activeCampaignObject, CustomDataAttribute dataAttribute) {
        Schema schema = getFieldSchema(activeCampaignObject, dataAttribute);
        AtomicReference<String> schemaType = null;
        Optional.ofNullable(schema).ifPresent(schema1 -> schemaType.set(schema1.getType().getDisplayName()));
        return schemaType.get();
    }

    public static Schema getFieldSchema(ActiveCampaignObject activeCampaignObject, CustomDataAttribute dataAttribute) {
        switch (dataAttribute.getType()) {
            case "textarea":
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case "text":
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case "dropdown":
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case "radio":
                return SchemaConstants.OPTIONAL_BOOL_SCHEMA;
            case "checkbox":
                return SchemaConstants.OPTIONAL_BOOL_SCHEMA;
            case "listbox":
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case "hidden":
                return SchemaConstants.OPTIONAL_STRING_SCHEMA;
            case "date":
                return SchemaConstants.OPTIONAL_DATE_SCHEMA;
            case "datetime":
                return SchemaConstants.OPTIONAL_TIMESTAMP_SCHEMA;
            default:
                log.warn("Invalid data type %s", dataAttribute.getType());
                return null;
        }
    }
}
