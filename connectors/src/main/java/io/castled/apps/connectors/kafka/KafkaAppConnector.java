package io.castled.apps.connectors.kafka;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.commons.models.AppSyncMode;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.exceptions.connect.InvalidConfigException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.mapping.MappingGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;

import javax.inject.Singleton;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class KafkaAppConnector implements ExternalAppConnector<KafkaAppConfig, KafkaDataWriter, KafkaAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(KafkaAppConfig config, KafkaAppSyncConfig mappingConfig) {
        Properties properties = new Properties();
        try {
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            try (AdminClient adminClient = KafkaAdminClient.create(properties)) {
                return adminClient.listTopics().names().get()
                        .stream().map(topic -> new FormFieldOption(topic, topic)).collect(Collectors.toList());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Topics list failed for kafka", e);
            throw new CastledRuntimeException(e);
        }
    }

    public void validateAppConfig(KafkaAppConfig kafkaAppConfig) throws InvalidConfigException {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAppConfig.getBootstrapServers());
        try (AdminClient ignored = KafkaAdminClient.create(properties)) {
        } catch (KafkaException e) {
            String rootCause = ExceptionUtils.getRootCauseMessage(e);
            throw new InvalidConfigException(rootCause);
        }
    }

    @Override
    public KafkaDataWriter getDataSink() {
        return ObjectRegistry.getInstance(KafkaDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(KafkaAppConfig config, KafkaAppSyncConfig kafkaAppSyncConfig) {
        return new ExternalAppSchema(null);
    }

    @Override
    public Class<KafkaAppSyncConfig> getMappingConfigType() {
        return KafkaAppSyncConfig.class;
    }

    @Override
    public Class<KafkaAppConfig> getAppConfigType() {
        return KafkaAppConfig.class;
    }

    public List<AppSyncMode> getSyncModes(KafkaAppConfig kafkaAppConfig, KafkaAppSyncConfig kafkaAppSyncConfig) {
        return Lists.newArrayList(AppSyncMode.INSERT);
    }

    public List<MappingGroup> getMappingGroups(KafkaAppConfig config, KafkaAppSyncConfig kafkaAppSyncConfig) {
        MappingGroupAggregator.Builder builder = MappingGroupAggregator.builder();
        return builder.addElasticAppFields(true, false).build().getMappingGroups();
    }
}
