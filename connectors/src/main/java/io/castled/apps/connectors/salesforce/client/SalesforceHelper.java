package io.castled.apps.connectors.salesforce.client;

import io.castled.apps.connectors.salesforce.SalesforceAccessConfig;
import io.castled.apps.connectors.salesforce.SalesforceObjectFields;
import io.castled.apps.connectors.salesforce.client.dtos.SalesforceField;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.SchemaConstants;
import io.castled.schema.models.RecordSchema;
import io.castled.schema.models.Schema;

import java.util.List;

public class SalesforceHelper {

    private static final String SFDC_MAX_VERSION = "47.0";

    public static String getServicesUrl(SalesforceAccessConfig accessConfig) {
        return String.format("%s/services/data/%s", accessConfig.getInstanceUrl(), "v" + SFDC_MAX_VERSION);
    }

    public static String getBulkUrl(SalesforceAccessConfig accessConfig) {
        return String.format("%s/services/async/%s", accessConfig.getInstanceUrl(), SFDC_MAX_VERSION);
    }

    public static RecordSchema getRecordSchema(String object, List<SalesforceField> salesforceFields) {
        RecordSchema.Builder recordBuilder = RecordSchema.builder().name(object);
        for (SalesforceField objectField : salesforceFields) {
            Schema fieldSchema = getSalesforceFieldSchema(objectField);
            if (fieldSchema != null) {
                recordBuilder.put(objectField.getName(), fieldSchema);
            }
        }
        return recordBuilder.build();
    }

    public static Schema getSalesforceFieldSchema(SalesforceField salesforceField) {
        String soapType = salesforceField.getSoapType().split(":")[1];
        SFDCObjectType sfdcObjectType = SFDCObjectType.fromName(soapType);
        if (sfdcObjectType == null) {
            return null;
        }
        Schema schema = null;
        switch (sfdcObjectType) {
            case DATE:
                schema = SchemaConstants.DATE_SCHEMA;
                break;
            case DATETIME:
                schema = SchemaConstants.ZONED_TIMESTAMP_SCHEMA;
                break;
            case INTEGER:
                schema = SchemaConstants.LONG_SCHEMA;
                break;
            case TIME:
                schema = SchemaConstants.TIME_SCHEMA;
                break;
            case STRING:
            case ID:
                schema = SchemaConstants.STRING_SCHEMA;
                break;
            case BOOLEAN:
                schema = SchemaConstants.BOOL_SCHEMA;
                break;
            case DOUBLE:
                if (salesforceField.getScale() > 0) {
                    schema = SchemaConstants.DOUBLE_SCHEMA;
                } else {
                    schema = SchemaConstants.LONG_SCHEMA;
                }
                break;
            default:
                throw new CastledRuntimeException("Invalid soapType " + sfdcObjectType);
        }
        schema.setOptional(true);
        return schema;
    }

    public static boolean checkPkEligibility(SalesforceField salesforceField) {
        if (salesforceField.getName().equals(SalesforceObjectFields.ID)) {
            return false;
        }
        return salesforceField.isExternalId() || salesforceField.isIdLookup()
                || salesforceField.isUnique();
    }
}
