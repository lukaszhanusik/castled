package io.castled.apps.connectors.intercom;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.ExternalAppType;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.apps.connectors.intercom.client.IntercomObjectFields;
import io.castled.apps.connectors.intercom.client.IntercomRestClient;
import io.castled.apps.connectors.intercom.client.dtos.DataAttribute;
import io.castled.apps.connectors.intercom.client.models.IntercomModel;
import io.castled.commons.models.AppSyncMode;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.mapping.QuestionnaireGroupField;
import io.castled.schema.mapping.MappingGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class IntercomAppConnector implements ExternalAppConnector<IntercomAppConfig, IntercomDataWriter,
        IntercomAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(IntercomAppConfig config, IntercomAppSyncConfig mappingConfig) {
        return Arrays.stream(IntercomObject.values()).map(intercomObject -> new FormFieldOption(new GenericSyncObject(intercomObject.getName(),
                ExternalAppType.INTERCOM), intercomObject.getName())).collect(Collectors.toList());
    }

    @Override
    public IntercomDataWriter getDataSink() {
        return ObjectRegistry.getInstance(IntercomDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(IntercomAppConfig config, IntercomAppSyncConfig mappingConfig) {
        IntercomRestClient intercomRestClient = new IntercomRestClient(config.getAccessToken());
        IntercomObject intercomObject = IntercomObject.getObjectByName(mappingConfig.getObject().getObjectName());
        IntercomModel intercomModel = IntercomUtils.getIntercomModel(intercomObject);
        List<DataAttribute> dataAttributes = intercomRestClient.listAttributes(intercomModel);
        return new ExternalAppSchema(IntercomUtils.getSchema(intercomObject, dataAttributes));
    }

    @Override
    public Class<IntercomAppSyncConfig> getMappingConfigType() {
        return IntercomAppSyncConfig.class;
    }

    public List<String> getPrimaryKeyEligibles(IntercomModel intercomModel) {
        switch (intercomModel) {
            case CONTACT:
                return Lists.newArrayList(IntercomObjectFields.EMAIL, IntercomObjectFields.EXTERNAL_USER_ID);
            case COMPANY:
                return Lists.newArrayList(IntercomObjectFields.COMPANY_ID);
            default:
                throw new CastledRuntimeException(String.format("Model %s not supported", intercomModel));
        }
    }

    public List<AppSyncMode> getSyncModes(IntercomAppConfig config, IntercomAppSyncConfig mappingConfig) {
        return Lists.newArrayList(AppSyncMode.UPDATE, AppSyncMode.UPSERT);
    }

    private List<QuestionnaireGroupField> getQuestionnaireFields(IntercomAppSyncConfig intercomAppSyncConfig) {
        if (!intercomAppSyncConfig.isAssociateCompany()) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(new QuestionnaireGroupField("Which source column contains the Company Id to associate contacts to companies", null,
                false, IntercomObjectFields.COMPANY_ID));
    }

    public List<MappingGroup> getMappingGroups(IntercomAppConfig intercomAppConfig, IntercomAppSyncConfig intercomAppSyncConfig) {
        IntercomRestClient intercomRestClient = new IntercomRestClient(intercomAppConfig.getAccessToken());
        IntercomObject intercomObject = IntercomObject.getObjectByName(intercomAppSyncConfig.getObject().getObjectName());
        IntercomModel intercomModel = IntercomUtils.getIntercomModel(intercomObject);
        List<DataAttribute> dataAttributes = intercomRestClient.listAttributes(intercomModel);
        return MappingGroupAggregator.builder().addQuestionnaireFields(getQuestionnaireFields(intercomAppSyncConfig))
                .addPrimaryKeyFields(getPrimaryKeyGroupFields(intercomModel))
                .addFixedAppFields(getFixedGroupAppFields(intercomModel, dataAttributes)).build().getMappingGroups();

    }

    private List<PrimaryKeyGroupField> getPrimaryKeyGroupFields(IntercomModel intercomModel) {
        return getPrimaryKeyEligibles(intercomModel).stream().map(field -> new PrimaryKeyGroupField(field, field, true))
                .collect(Collectors.toList());

    }

    private List<FixedGroupAppField> getFixedGroupAppFields(IntercomModel intercomModel, List<DataAttribute> dataAttributes) {
        if (intercomModel == IntercomModel.CONTACT) {
            return dataAttributes.stream().map(DataAttribute::getName).map(field -> new FixedGroupAppField(field, field, true))
                    .collect(Collectors.toList());
        }
        return dataAttributes.stream().map(DataAttribute::getName).filter(field -> !getPrimaryKeyEligibles(intercomModel).contains(field))
                .map(field -> new FixedGroupAppField(field, field, true)).collect(Collectors.toList());

    }

    @Override
    public Class<IntercomAppConfig> getAppConfigType() {
        return IntercomAppConfig.class;
    }
}
