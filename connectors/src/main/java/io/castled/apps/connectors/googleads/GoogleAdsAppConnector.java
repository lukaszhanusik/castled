package io.castled.apps.connectors.googleads;

import com.google.ads.googleads.v7.resources.ConversionCustomVariable;
import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.dtos.PipelineConfigDTO;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.models.FieldMapping;
import io.castled.models.TargetFieldsMapping;
import io.castled.schema.SchemaConstants;
import io.castled.schema.mapping.MappingGroup;
import io.castled.schema.models.RecordSchema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.ws.rs.BadRequestException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleAdsAppConnector implements ExternalAppConnector<GoogleAdsAppConfig, GoogleAdsDataWriter, GoogleAdsAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(GoogleAdsAppConfig config, GoogleAdsAppSyncConfig mappingConfig) {
        return Arrays.stream(GAdsObjectType.values()).map(this::getFormSelectOption).collect(Collectors.toList());
    }

    @Override
    public ExternalAppSchema getSchema(GoogleAdsAppConfig config, GoogleAdsAppSyncConfig mappingConfig) {

        switch (mappingConfig.getObjectType()) {
            case CUSTOMER_MATCH:
                return getSchemaForCustomerMatch(mappingConfig.getCustomerMatchType());
            case CLICK_CONVERSIONS:
                return getSchemaForClickConversions(config, mappingConfig);
            case CALL_CONVERSIONS:
                return getSchemaForCallConversions(config, mappingConfig);
        }

        throw new CastledRuntimeException(String.format("Unhandled object type %s", mappingConfig.getObjectType()));
    }

    private Pair<String, String> getTitleAndDescription(GAdsObjectType gAdsObjectType) {
        switch (gAdsObjectType) {
            case CUSTOMER_MATCH:
                return Pair.of("Customer match list", "Sync customized users list to the customer match list to be used in ad campaigns");
            case CALL_CONVERSIONS:
                return Pair.of("Call Conversions", "Sync call conversion metrics to the call conversions object for better evaluation of your ad campaigns");
            case CLICK_CONVERSIONS:
                return Pair.of("Click Conversions", "Sync click conversion metrics to the click conversions object for better evaluation of your ad campaigns");
            default:
                throw new CastledRuntimeException(String.format("Invalid google ads object type %s", gAdsObjectType));
        }
    }

    private FormFieldOption getFormSelectOption(GAdsObjectType gAdsObjectType) {
        Pair<String, String> titleAndDescription = getTitleAndDescription(gAdsObjectType);
        return new FormFieldOption(gAdsObjectType, titleAndDescription.getLeft(), titleAndDescription.getRight());
    }

    @Override
    public GoogleAdsDataWriter getDataSink() {
        return ObjectRegistry.getInstance(GoogleAdsDataWriter.class);
    }

    private ExternalAppSchema getSchemaForCallConversions(GoogleAdsAppConfig googleAdsAppConfig,
                                                          GoogleAdsAppSyncConfig mappingConfig) {
        RecordSchema.Builder customerSchemaBuilder = RecordSchema.builder();
        for (GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS field : GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.values()) {
            customerSchemaBuilder.put(field.getFieldName(), field.getSchema());
        }
        for (String customVariable : GoogleAdUtils.getCustomVariables(googleAdsAppConfig, mappingConfig).stream().map(ConversionCustomVariable::getName).collect(Collectors.toList())) {
            customerSchemaBuilder.put(customVariable, SchemaConstants.OPTIONAL_STRING_SCHEMA);
        }
        return new ExternalAppSchema(customerSchemaBuilder.build());
    }


    private ExternalAppSchema getSchemaForClickConversions(GoogleAdsAppConfig googleAdsAppConfig,
                                                           GoogleAdsAppSyncConfig googleAdsMappingConfig) {
        RecordSchema.Builder recordSchemaBuilder = RecordSchema.builder();
        for (GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS field : GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.values()) {
            recordSchemaBuilder.put(field.getFieldName(), field.getSchema());
        }
        for (String customVariable : GoogleAdUtils.getCustomVariables(googleAdsAppConfig, googleAdsMappingConfig).stream().map(ConversionCustomVariable::getName).collect(Collectors.toList())) {
            recordSchemaBuilder.put(customVariable, SchemaConstants.OPTIONAL_STRING_SCHEMA);
        }
        return new ExternalAppSchema(recordSchemaBuilder.build());
    }

    private ExternalAppSchema getSchemaForCustomerMatch(CustomerMatchType customerMatchType) {
        switch (customerMatchType) {
            case CONTACT_INFO:
                RecordSchema.Builder customerSchemaBuilder = RecordSchema.builder();
                for (GadsObjectFields.CUSTOMER_MATCH_CONTACT_INFO_FIELDS customerMatchField : GadsObjectFields.CUSTOMER_MATCH_CONTACT_INFO_FIELDS.values()) {
                    customerSchemaBuilder.put(customerMatchField.getFieldName(), SchemaConstants.OPTIONAL_STRING_SCHEMA);
                }
                return new ExternalAppSchema(customerSchemaBuilder.build());

            case CRM_ID:
                RecordSchema.Builder userIdSchemaBuilder = RecordSchema.builder()
                        .put(GadsObjectFields.CUSTOMER_MATCH_USER_ID_FIELD, SchemaConstants.STRING_SCHEMA);
                return new ExternalAppSchema(userIdSchemaBuilder.build());

            case MOBILE_ADVERTISING_ID:
                RecordSchema.Builder mobileIdSchemaBuilder = RecordSchema.builder()
                        .put(GadsObjectFields.CUSTOMER_MATCH_MOBILE_DEVICE_ID_FIELD, SchemaConstants.STRING_SCHEMA);

                return new ExternalAppSchema(mobileIdSchemaBuilder.build());

            default:
                throw new CastledRuntimeException(String.format("Invalid customer match type %s", customerMatchType));

        }
    }

    public PipelineConfigDTO validateAndEnrichPipelineConfig(PipelineConfigDTO pipelineConfig) throws BadRequestException {
        TargetFieldsMapping targetFieldsMapping = (TargetFieldsMapping) pipelineConfig.getMapping();
        List<String> mappedAppFields = targetFieldsMapping.getFieldMappings().stream()
                .map(FieldMapping::getAppField).collect(Collectors.toList());
        GAdsObjectType gAdsObjectType = ((GoogleAdsAppSyncConfig) pipelineConfig.getAppSyncConfig()).getObjectType();
        List<String> requiredFields = getRequiredFields(gAdsObjectType);
        List<String> missingFields = ListUtils.subtract(requiredFields, mappedAppFields);
        if (CollectionUtils.isNotEmpty(missingFields)) {
            throw new BadRequestException(String.format("Mandatory fields %s not mapped", String.join(",", missingFields)));
        }
        return pipelineConfig;
    }

    private List<String> getRequiredFields(GAdsObjectType gAdsObjectType) {
        switch (gAdsObjectType) {
            case CLICK_CONVERSIONS:
                return Lists.newArrayList(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName(),
                        GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.GCLID.getFieldName());
            case CALL_CONVERSIONS:
                return Lists.newArrayList(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName(),
                        GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALLER_ID.getFieldName(), GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALL_START_TIME.getFieldName());
            default:
                return Lists.newArrayList();
        }
    }

    public Class<GoogleAdsAppSyncConfig> getMappingConfigType() {
        return GoogleAdsAppSyncConfig.class;
    }

    @Override
    public Class<GoogleAdsAppConfig> getAppConfigType() {
        return GoogleAdsAppConfig.class;
    }

    @Override
    public List<MappingGroup> getMappingGroups(GoogleAdsAppConfig googleAdsAppConfig, GoogleAdsAppSyncConfig googleAdsAppSyncConfig) {
        switch (googleAdsAppSyncConfig.getObjectType()) {
            case CUSTOMER_MATCH:
                return getCustomerMatchMappingGroup(googleAdsAppSyncConfig.getCustomerMatchType());
            case CALL_CONVERSIONS:
                return getCallConversionMappingGroup();
            case CLICK_CONVERSIONS:
                return getClickConversionMappingGroup();
            default:
                throw new CastledRuntimeException(String.format("Invalid object type %s", googleAdsAppSyncConfig.getObjectType()));
        }
    }

    private List<MappingGroup> getCustomerMatchMappingGroup(CustomerMatchType customerMatchType) {
        switch (customerMatchType) {
            case CRM_ID:
                return getUserIdMappingGroup();
            case MOBILE_ADVERTISING_ID:
                return getMobileIdMappingGroup();
            case CONTACT_INFO:
                return getContactInfoMappingGroup();
            default:
                throw new CastledRuntimeException(String.format("Invalid customer match type %s", customerMatchType));
        }
    }

    private List<MappingGroup> getContactInfoMappingGroup() {
        List<FixedGroupAppField> fixedGroupAppFields = Arrays.stream(GadsObjectFields.CUSTOMER_MATCH_CONTACT_INFO_FIELDS.values())
                .map(field -> new FixedGroupAppField(field.getFieldName(), field.getFieldName(), true)).collect(Collectors.toList());
        return MappingGroupAggregator.builder().addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();

    }

    private List<MappingGroup> getUserIdMappingGroup() {
        List<FixedGroupAppField> fixedGroupAppFields = Lists.newArrayList(new FixedGroupAppField(GadsObjectFields.CUSTOMER_MATCH_USER_ID_FIELD,
                GadsObjectFields.CUSTOMER_MATCH_USER_ID_FIELD, true));
        return MappingGroupAggregator.builder().addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();

    }

    private List<MappingGroup> getMobileIdMappingGroup() {
        List<FixedGroupAppField> fixedGroupAppFields = Lists.newArrayList(new FixedGroupAppField(GadsObjectFields.CUSTOMER_MATCH_MOBILE_DEVICE_ID_FIELD,
                GadsObjectFields.CUSTOMER_MATCH_MOBILE_DEVICE_ID_FIELD, true));
        return MappingGroupAggregator.builder().addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();
    }

    private List<MappingGroup> getClickConversionMappingGroup() {
        List<String> requiredFields = Lists.newArrayList(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.GCLID.getFieldName(),
                GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName());
        List<FixedGroupAppField> fixedGroupAppFields = requiredFields.stream().map(field -> new FixedGroupAppField(field, field, false))
                .collect(Collectors.toList());
        fixedGroupAppFields.addAll(Arrays.stream(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.values()).map(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS::getFieldName)
                .filter(field -> !requiredFields.contains(field)).map(field -> new FixedGroupAppField(field, field, true)).collect(Collectors.toList()));
        return MappingGroupAggregator.builder().addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();
    }

    private List<MappingGroup> getCallConversionMappingGroup() {
        List<String> requiredFields = Lists.newArrayList(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALLER_ID.getFieldName(),
                GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName(), GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALL_START_TIME.getFieldName());
        List<FixedGroupAppField> fixedGroupAppFields = requiredFields.stream().map(field -> new FixedGroupAppField(field, field, false))
                .collect(Collectors.toList());
        fixedGroupAppFields.addAll(Arrays.stream(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.values()).map(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS::getFieldName)
                .filter(field -> !requiredFields.contains(field)).map(field -> new FixedGroupAppField(field, field, true)).collect(Collectors.toList()));
        return MappingGroupAggregator.builder().addFixedAppFields(fixedGroupAppFields).build().getMappingGroups();
    }
}
