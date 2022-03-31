package io.castled.apps.connectors.Iterable;

import com.google.common.collect.Lists;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.connectors.Iterable.client.IterableRestClient;
import io.castled.apps.connectors.Iterable.client.dtos.*;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IterableAppConnector implements ExternalAppConnector<IterableAppConfig, IterableDataWriter, IterableSyncConfig> {
    @Override
    public List<FormFieldOption> getAllObjects(IterableAppConfig config, IterableSyncConfig mappingConfig) {
        return Arrays.stream(IterableObject.values()).map(obj -> new FormFieldOption(new GenericSyncObject(obj.getName(),
                ExternalAppType.ITERABLE), obj.getName())).collect(Collectors.toList());
    }

    @Override
    public List<AppSyncMode> getSyncModes(IterableAppConfig config, IterableSyncConfig appSyncConfig) {
        IterableObject obj = IterableSchemaUtils.getIterableObject(appSyncConfig.getObject());
        switch (obj) {
            default:
                return Lists.newArrayList(AppSyncMode.UPSERT);
        }
    }

    @Override
    public IterableDataWriter getDataSink() {
        return new IterableDataWriter();
    }

    @Override
    public ExternalAppSchema getSchema(IterableAppConfig config, IterableSyncConfig iterableSyncConfig) {

        switch (IterableSchemaUtils.getIterableObject(iterableSyncConfig.getObject())) {
            case USERS: {
                IterableRestClient client = new IterableRestClient(config);
                Map<String, String> userFields = client.getUserFields();
                return new ExternalAppSchema(IterableSchemaUtils.getSchema(iterableSyncConfig, userFields));
            }
            case EVENTS: {
                Map<String, String> eventFields = Arrays.stream(EventField.values()).collect(Collectors.toMap(EventField::getName, EventField::getType));
                return new ExternalAppSchema(IterableSchemaUtils.getSchema(iterableSyncConfig, eventFields));
            }
            case CATALOGS: {
                IterableRestClient client = new IterableRestClient(config);
                Map<String, String> catalogItemFields = client.getCatalogFieldMappings(iterableSyncConfig.getCatalogName());
                Arrays.stream(CatalogItemField.values()).forEach(itemField -> catalogItemFields.put(itemField.getName(), itemField.getType()));
                return new ExternalAppSchema(IterableSchemaUtils.getSchema(iterableSyncConfig, catalogItemFields));
            }
            default:
                throw new CastledRuntimeException("Not supported!");
        }
    }

    @Override
    public List<MappingGroup> getMappingGroups(IterableAppConfig config, IterableSyncConfig syncConfig) {

        switch (IterableSchemaUtils.getIterableObject(syncConfig.getObject())) {
            case USERS:
                return getUserMappingGroups(config, syncConfig);
            case EVENTS:
                return getEventMappingGroups();
            case CATALOGS:
                return getCatalogMappingGroups(config, syncConfig);
            default:
                throw new CastledRuntimeException("Not supported!");
        }
    }

    public List<MappingGroup> getUserMappingGroups(IterableAppConfig config, IterableSyncConfig iterableSyncConfig) {

        IterableRestClient client = new IterableRestClient(config);
        Map<String, String> fields = client.getUserFields();
        List<String> fieldNames = fields.keySet().stream().sorted().collect(Collectors.toList());

        // Primary key group
        List<PrimaryKeyGroupField> primaryKeyGroupFields = Lists.newArrayList();
        primaryKeyGroupFields.add(PrimaryKeyGroupField.builder()
                .name(FieldConsts.EMAIL)
                .displayName(FieldConsts.EMAIL)
                .optional(false)
                .build());

        // Fixed field group
        List<FixedGroupAppField> fixedGroupAppFields = fieldNames.stream()
                .filter(fieldName -> !FieldConsts.EMAIL.contains(fieldName))
                .map(fieldName -> new FixedGroupAppField(fieldName, fieldName, true))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields(fixedGroupAppFields)
                .addElasticAppFields("Data fields to store in the user profile",
                        "If you want to add a new field to the Iterable user profile please use this section. " +
                                "The new field created will be updated with the value of the mapped source column",
                        false, false)
                .build().getMappingGroups();
    }

    public List<MappingGroup> getEventMappingGroups() {

        // Primary key group
        List<PrimaryKeyGroupField> primaryKeyGroupFields = Arrays.stream(EventPrimaryKey.values())
                .map(eventField -> new PrimaryKeyGroupField(eventField.getName(), eventField.getName(), true))
                .collect(Collectors.toList());

        // Fixed group with email or user id.
        List<FixedGroupAppField> fixedGroupUserFields = Arrays.stream(UserPrimaryKey.values())
                .map(userField -> new FixedGroupAppField(userField.getName(), userField.getName(), true))
                .collect(Collectors.toList());

        // Fixed field group
        List<FixedGroupAppField> fixedGroupAppFields = Arrays.stream(EventField.values())
                .filter(eventField -> !IterableSchemaUtils.isUserPrimaryKey(eventField.getName()))
                .map(eventField -> new FixedGroupAppField(eventField.getName(), eventField.getName(), eventField.isOptional()))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields("User fields for the event",
                        "Either email or userId must be passed in to identify the user. If both are passed in, email takes precedence.",
                        fixedGroupUserFields)
                .addFixedAppFields(fixedGroupAppFields)
                .addElasticAppFields("Custom properties for the event",
                        "Additional data associated with event (i.e. item amount, item quantity). " +
                                "For events of the same name, identically named data fields must be of the same type.",
                        false, false)
                .build().getMappingGroups();
    }

    public List<MappingGroup> getCatalogMappingGroups(IterableAppConfig config, IterableSyncConfig iterableSyncConfig) {

        // Primary key group
        List<PrimaryKeyGroupField> primaryKeyGroupFields = Arrays.stream(CatalogItemField.values())
                .map(catalogItemField -> new PrimaryKeyGroupField(catalogItemField.getName(), catalogItemField.getName(), false))
                .collect(Collectors.toList());

        // Fixed field group
        IterableRestClient client = new IterableRestClient(config);
        Map<String, String> fields = client.getCatalogFieldMappings(iterableSyncConfig.getCatalogName());
        List<String> fieldNames = fields.keySet().stream().sorted().collect(Collectors.toList());
        List<FixedGroupAppField> fixedGroupAppFields = fieldNames.stream()
                .map(fieldName -> new FixedGroupAppField(fieldName, fieldName, true))
                .collect(Collectors.toList());

        return MappingGroupAggregator.builder()
                .addPrimaryKeyFields(primaryKeyGroupFields)
                .addFixedAppFields(fixedGroupAppFields)
                .addElasticAppFields("New fields for the catalog item",
                        "A new catalog item field will be created with the given name, and will be updated with the value of the mapped source column.",
                        false, false)
                .build().getMappingGroups();
    }

    @Override
    public Class<IterableSyncConfig> getMappingConfigType() {
        return IterableSyncConfig.class;
    }

    @Override
    public Class<IterableAppConfig> getAppConfigType() {
        return IterableAppConfig.class;
    }
}
