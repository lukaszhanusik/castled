package io.castled.apps.connectors.kafka;

import com.google.common.collect.Sets;
import io.castled.ObjectRegistry;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.kafka.producer.CastledKafkaProducer;
import io.castled.kafka.producer.CastledProducerCallback;
import io.castled.kafka.producer.KafkaProducerConfiguration;
import io.castled.utils.MessageUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class KafkaDataWriter implements DataWriter {

    private final AtomicLong recordsProcessed = new AtomicLong(0);
    private final Set<Long> pendingMessageIds = Sets.newConcurrentHashSet();
    private long lastBufferedOffset = 0;
    private volatile Exception throwable;

    public class DataSinkCallback implements CastledProducerCallback {

        private final long messageOffset;

        public DataSinkCallback(long messageOffset) {
            this.messageOffset = messageOffset;
        }

        @Override
        public void onSuccess(RecordMetadata recordMetadata) {
            recordsProcessed.incrementAndGet();
            pendingMessageIds.remove(messageOffset);
        }

        @Override
        public void onFailure(RecordMetadata recordMetadata, Exception e) {
            throwable = e;
        }
    }

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        KafkaAppConfig kafkaAppConfig = (KafkaAppConfig) dataWriteRequest.getExternalApp().getConfig();
        KafkaAppSyncConfig kafkaAppSyncConfig = (KafkaAppSyncConfig) dataWriteRequest.getAppSyncConfig();
        DataSinkMessage message;
        try (CastledKafkaProducer kafkaProducer = new CastledKafkaProducer
                (KafkaProducerConfiguration.builder().bootstrapServers(kafkaAppConfig.getBootstrapServers()).build())) {
            while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
                validateAndThrow();
                pendingMessageIds.add(message.getOffset());
                publishMessage(kafkaProducer, message, kafkaAppSyncConfig.getTopic(),
                        dataWriteRequest.getErrorOutputStream());
                lastBufferedOffset = message.getOffset();
            }
            kafkaProducer.flush();
            validateAndThrow();
        }
    }

    private void publishMessage(CastledKafkaProducer kafkaProducer, DataSinkMessage message, String topic,
                                ErrorOutputStream errorOutputStream) {
        try {
            kafkaProducer.publish(new ProducerRecord<>(topic, null,
                    MessageUtils.messageToBytes(message.getMessage())), new DataSinkCallback(message.getOffset()));
        } catch (Exception e) {
            pendingMessageIds.remove(message.getOffset());
            recordsProcessed.incrementAndGet();
            errorOutputStream.writeFailedRecord(message, ObjectRegistry.getInstance(KafkaErrorParser.class)
                    .parseException(e));
        }
    }

    private void validateAndThrow() throws Exception {
        if (throwable != null) {
            throw throwable;
        }
    }

    @Override
    public AppSyncStats getSyncStats() {
        return new AppSyncStats(recordsProcessed.get(), getProcessedOffset(), 0);
    }

    public long getProcessedOffset() {
        try {
            long currentMinPendingId = Collections.min(pendingMessageIds);
            return currentMinPendingId - 1;
        } catch (NoSuchElementException e) {
            return lastBufferedOffset;
        }
    }
}