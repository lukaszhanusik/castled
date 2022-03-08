package io.castled.apps.connectors.fbconversion;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.castled.apps.BufferedObjectSink;
import io.castled.apps.connectors.fbconversion.client.FbConversionClient;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomDataField;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomerInfoField;
import io.castled.apps.connectors.fbconversion.client.dtos.ServerEventAggregatedField;
import io.castled.apps.connectors.fbconversion.client.dtos.ServerEventField;
import io.castled.commons.errors.errorclassifications.InvalidFieldValueError;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.schema.models.Field;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FbConversionBufferedSink extends BufferedObjectSink<DataSinkMessage> {

    private final long BATCH_SIZE = 1000;
    private final FbConversionClient fbConversionClient;
    private final ErrorOutputStream errorOutputStream;
    private final AppSyncStats appSyncStats;
    private final FbConversionSyncConfig fbConversionSyncConfig;

    private final Map<String, ServerEventField> serverEventFieldMap;
    private final Map<String, ServerEventAggregatedField> serverEventAggregatedFieldMap;
    private final Map<String, CustomerInfoField> customerInfoFieldMap;
    private final Map<String, CustomDataField> customDataFieldMap;

    FbConversionBufferedSink(FbConversionAppConfig fbConversionAppConfig, FbConversionSyncConfig fbConversionSyncConfig,
                             ErrorOutputStream errorOutputStream) {
        this.fbConversionClient = new FbConversionClient(fbConversionAppConfig, fbConversionSyncConfig);
        this.errorOutputStream = errorOutputStream;
        this.appSyncStats = new AppSyncStats();
        this.fbConversionSyncConfig = fbConversionSyncConfig;

        this.serverEventFieldMap = Arrays.stream(ServerEventField.values())
                .collect(Collectors.toMap(ServerEventField::getName, Function.identity()));

        this.serverEventAggregatedFieldMap = Arrays.stream(ServerEventAggregatedField.values())
                .collect(Collectors.toMap(ServerEventAggregatedField::getName, Function.identity()));

        this.customerInfoFieldMap = Arrays.stream(CustomerInfoField.values())
                .collect(Collectors.toMap(CustomerInfoField::getName, Function.identity()));

        this.customDataFieldMap = Arrays.stream(CustomDataField.values())
                .collect(Collectors.toMap(CustomDataField::getName, Function.identity()));
    }

    @Override
    protected void writeRecords(List<DataSinkMessage> msgs) {

        List<Map<String, Object>> requestData = getRequestData(msgs);
        FbConversionStatus status = this.fbConversionClient.sendConversionEvents(requestData);
        if (status.getEventsReceived() == 0) {
            // Entire batch gets rejected if a single record is invalid.
            for (DataSinkMessage msg : msgs) {
                this.errorOutputStream.writeFailedRecord(msg, new InvalidFieldValueError(null,
                        status.getErrorUserTitle(), status.getErrorUserMsg()));
            }
        }
        updateStats(msgs.size(), Iterables.getLast(msgs).getOffset());
    }

    @Override
    public long getMaxBufferedObjects() {
        return BATCH_SIZE;
    }

    List<Map<String, Object>> getRequestData(List<DataSinkMessage> records) {
        List<Map<String, Object>> requestData = Lists.newArrayList();
        for (DataSinkMessage msg : records) {
            Map<String, Object> serverEvent = Maps.newHashMap();
            Map<String, Object> customerInfo = Maps.newHashMap();
            Map<String, Object> customDataInfo = Maps.newHashMap();

            for (Field field : msg.getRecord().getFields()) {
                if (serverEventFieldMap.containsKey(field.getName())) {
                    serverEvent.put(field.getName(), FbConversionFormatUtils.formatValue(field.getValue(),
                            serverEventFieldMap.get(field.getName())));
                } else if (customerInfoFieldMap.containsKey(field.getName())) {
                    // Normalize
                    CustomerInfoField customerInfoField = customerInfoFieldMap.get(field.getName());
                    String outVal = FbConversionFormatUtils.formatValue(field.getValue(), customerInfoField);
                    //  Hash PII if required
                    if (fbConversionSyncConfig.isHashingRequired()) {
                        outVal = FbConversionFormatUtils.hashValue(outVal, customerInfoField);
                    }
                    customerInfo.put(field.getName(), outVal);
                } else if (customDataFieldMap.containsKey(field.getName())) {
                    customDataInfo.put(field.getName(), FbConversionFormatUtils.formatValue(field.getValue(),
                            customDataFieldMap.get(field.getName())));
                } else {
                    // This should be a custom property.
                    customDataInfo.put(field.getName(), field.getValue());
                }
            }
            serverEvent.put(ServerEventAggregatedField.USER_DATA.getName(), customerInfo);
            if (!customDataInfo.isEmpty()) {
                serverEvent.put(ServerEventAggregatedField.CUSTOM_DATA.getName(), customDataInfo);
            }
            // If `action_source` server event field not mapped, fetch the one from app sync config.
            if (!serverEvent.containsKey(ServerEventField.ACTION_SOURCE.getName())) {
                serverEvent.put(ServerEventField.ACTION_SOURCE.getName(), fbConversionSyncConfig.getActionSource());
            }
            requestData.add(serverEvent);
        }
        return requestData;
    }

    private void updateStats(long processed, long maxOffset) {
        appSyncStats.setRecordsProcessed(appSyncStats.getRecordsProcessed() + processed);
        appSyncStats.setOffset(Math.max(appSyncStats.getOffset(), maxOffset));
    }

    public AppSyncStats getSyncStats() {
        return appSyncStats;
    }
}
