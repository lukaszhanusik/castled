package io.castled.apps.connectors.mixpanel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.connectors.mixpanel.dto.EventAndError;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.errors.errorclassifications.UnclassifiedError;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.models.MessageSyncStats;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.core.CastledOffsetListQueue;
import io.castled.schema.models.Field;
import io.castled.schema.models.Tuple;
import io.castled.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


@Singleton
@Slf4j
public class MixpanelEventSink extends MixpanelObjectSink<DataSinkMessage> {

    private final MixpanelRestClient mixpanelRestClient;
    private final MixpanelErrorParser mixpanelErrorParser;
    private final ErrorOutputStream errorOutputStream;
    private final MixpanelAppSyncConfig mixpanelAppSyncConfig;
    private final AtomicLong processedRecords = new AtomicLong(0);
    private final CastledOffsetListQueue<DataSinkMessage> requestsBuffer =
            new CastledOffsetListQueue<>(new CreateEventConsumer(), 10, 10, true);
    private long lastProcessedOffset = 0;

    public MixpanelEventSink(DataWriteRequest dataWriteRequest) {
        this.mixpanelRestClient = new MixpanelRestClient(((MixpanelAppConfig) dataWriteRequest.getExternalApp().getConfig()).getProjectToken(),
                ((MixpanelAppConfig) dataWriteRequest.getExternalApp().getConfig()).getApiSecret());
        this.mixpanelAppSyncConfig = (MixpanelAppSyncConfig) dataWriteRequest.getAppSyncConfig();
        this.errorOutputStream = dataWriteRequest.getErrorOutputStream();
        this.mixpanelErrorParser = ObjectRegistry.getInstance(MixpanelErrorParser.class);
    }

    private void processBulkEventCreation(List<DataSinkMessage> messages) {
        List<EventAndError> failedRecords = this.mixpanelRestClient.insertEventDetails(
                messages.stream().map(DataSinkMessage::getRecord).map(this::constructEventDetails).collect(Collectors.toList()));

        Map<Object, DataSinkMessage> eventIDMapper = messages.stream().filter(message -> getEventID(message.getRecord()) != null)
                .collect(Collectors.toMap(message -> getEventID(message.getRecord()), Function.identity()));

        failedRecords.forEach(failedRecord ->
                failedRecord.getFailureReasons().forEach(failureReason -> this.errorOutputStream.writeFailedRecord(eventIDMapper.get(failedRecord.getInsertId()),
                        mixpanelErrorParser.getPipelineError(failureReason))));

        this.processedRecords.addAndGet(messages.size());
        this.lastProcessedOffset = Math.max(lastProcessedOffset, Iterables.getLast(messages).getOffset());
    }

    @Override
    protected void writeRecords(List<DataSinkMessage> messages) {
        try {
            requestsBuffer.writePayload(Lists.newArrayList(messages), 5, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            log.error("Unable to publish records to records queue", e);
            for (DataSinkMessage record : messages) {
                errorOutputStream.writeFailedRecord(record,
                        new UnclassifiedError("Internal error!! Unable to publish records to records queue. Please contact support"));
            }
        }
    }

    private Object getEventID(Tuple record) {
        return record.getValue(MixpanelObjectFields.EVENT_FIELDS.INSERT_ID.getFieldName());
    }

    private Map<String, Object> constructEventDetails(Tuple record) {
        Map<String, Object> eventInfo = Maps.newHashMap();
        eventInfo.put("event", this.mixpanelAppSyncConfig.getEventName());

        Map<String, Object> propertiesMap = Maps.newHashMap();
        propertiesMap.put("$" + MixpanelObjectFields.EVENT_FIELDS.INSERT_ID.getFieldName(),
                record.getValue(MixpanelObjectFields.EVENT_FIELDS.INSERT_ID.getFieldName()));
        propertiesMap.put(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName(),
                Optional.ofNullable(record.getValue(MixpanelObjectFields.EVENT_FIELDS.DISTINCT_ID.getFieldName())).orElse(""));
        propertiesMap.put(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName(),
                convertTimeStampToEpoch(record.getValue(MixpanelObjectFields.EVENT_FIELDS.EVENT_TIMESTAMP.getFieldName())));
        propertiesMap.put(MixpanelObjectFields.EVENT_FIELDS.GEO_IP.getFieldName(),
                record.getValue(MixpanelObjectFields.EVENT_FIELDS.GEO_IP.getFieldName()));
        //copy all non reserved properties from record
        propertiesMap.putAll(record.getFields().stream().
                filter(field -> !isMixpanelReservedKeyword(field.getName())).collect(Collectors.toMap(Field::getName, field -> transformFieldValue(field.getValue()))));
        eventInfo.put("properties", propertiesMap);

        return eventInfo;
    }

    private String transformFieldValue(Object object) {
        if (object instanceof Integer || object instanceof Long) {
            return String.valueOf(object);
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof LocalDate) {
            return ((LocalDate) object).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (object instanceof LocalDateTime) {
            return ((LocalDateTime) object).format(DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ"));
        }
        if (object instanceof ZonedDateTime) {
            return ((ZonedDateTime) object).format(DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ"));
        } else if (object instanceof Boolean) {
            return Boolean.toString((Boolean) object);
        }
        return StringUtils.EMPTY;
    }

    private Long convertTimeStampToEpoch(Object timestamp) {
        if (timestamp instanceof LocalDateTime) {
            return ((LocalDateTime) timestamp).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        if (timestamp instanceof ZonedDateTime) {
            return ((ZonedDateTime) timestamp).toInstant().toEpochMilli();
        }
        return null;
    }

    private boolean isMixpanelReservedKeyword(String fieldName) {
        return getReservedKeywords().contains(fieldName);
    }

    private List<String> getReservedKeywords() {
        return Lists.newArrayList("event", "time", "distinct_id", "insert_id", "ip");
    }

    public void flushRecords() throws Exception {
        super.flushRecords();
        requestsBuffer.flush(TimeUtils.minutesToMillis(10));
    }

    @Override
    public MessageSyncStats getSyncStats() {
        return new MessageSyncStats(processedRecords.get(), lastProcessedOffset);
    }

    @Override
    public long getMaxBufferedObjects() {
        return 2000;
    }

    private class CreateEventConsumer implements Consumer<List<DataSinkMessage>> {
        @Override
        public void accept(List<DataSinkMessage> messages) {
            if (CollectionUtils.isEmpty(messages)) {
                return;
            }
            processBulkEventCreation(messages);
        }
    }
}
