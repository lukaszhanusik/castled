package io.castled.apps.connectors.googlepubsub;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.common.collect.Lists;
import com.google.pubsub.v1.ProjectName;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.commons.models.ServiceAccountDetails;
import io.castled.exceptions.connect.InvalidConfigException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.mapping.MappingGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Singleton
@Slf4j
public class GooglePubSubAppConnector implements ExternalAppConnector<GooglePubSubAppConfig, GooglePubSubDataWriter, GooglePubSubAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(GooglePubSubAppConfig config, GooglePubSubAppSyncConfig mappingConfig) {
        List<FormFieldOption> formFieldOptions = Lists.newArrayList();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder().
                setCredentialsProvider(new GooglePubSubCredentialsProvider(config.getServiceAccountDetails())).build())) {

            topicAdminClient.listTopics(ProjectName.of(config.getProjectID())).iterateAll()
                    .forEach(topic -> formFieldOptions.add(new FormFieldOption(
                            new GooglePubSubTopicSyncObject(topic.getName().split("/")[3], topic.getName()), topic.getName())));

        } catch (IOException ioException) {
            log.error("Exception while fetching topics", ioException);
        }
        return formFieldOptions;
    }

    @Override
    public GooglePubSubDataWriter getDataSink() {
        return ObjectRegistry.getInstance(GooglePubSubDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(GooglePubSubAppConfig googlePubSubAppConfig, GooglePubSubAppSyncConfig googlePubSubAppSyncConfig) {
        return new ExternalAppSchema(null);
    }

    @Override
    public Class<GooglePubSubAppSyncConfig> getMappingConfigType() {
        return GooglePubSubAppSyncConfig.class;
    }

    @Override
    public Class<GooglePubSubAppConfig> getAppConfigType() {
        return GooglePubSubAppConfig.class;
    }

    public List<AppSyncMode> getSyncModes(GooglePubSubAppConfig googlePubSubAppConfig, GooglePubSubAppSyncConfig googlePubSubAppSyncConfig) {
        return Lists.newArrayList(AppSyncMode.INSERT);
    }

    public List<MappingGroup> getMappingGroups(GooglePubSubAppConfig config, GooglePubSubAppSyncConfig googlePubSubAppSyncConfig) {
        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addElasticAppFields(true, false).build().getMappingGroups();
    }

    public void validateAppConfig(GooglePubSubAppConfig appConfig) throws InvalidConfigException {
        String projectID = Optional.ofNullable(appConfig.getProjectID()).orElseThrow(() -> new InvalidConfigException("Project ID is mandatory"));
        ServiceAccountDetails serviceAccountDetails = Optional.ofNullable(appConfig.getServiceAccountDetails()).orElseThrow(() -> new InvalidConfigException("Service Account JSON is not uploaded"));

        List<String> topics = Lists.newArrayList();
        try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder().
                setCredentialsProvider(new GooglePubSubCredentialsProvider(serviceAccountDetails)).build())) {
            topicAdminClient.listTopics(ProjectName.of(projectID)).iterateAll().forEach(topic -> topics.add(topic.getName()));

            if (CollectionUtils.isEmpty(topics)) {
                throw new InvalidConfigException("Project don't have topics");
            }
        } catch (IOException ioException) {
            log.error("Exception while fetching topics", ioException);
            throw new InvalidConfigException("Config Incorrect");
        } catch (Exception exception) {
            throw new InvalidConfigException("Project ID/JSON entered is incorrect");
        }
    }
}
