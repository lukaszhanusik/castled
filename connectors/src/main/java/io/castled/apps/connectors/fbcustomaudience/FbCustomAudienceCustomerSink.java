package io.castled.apps.connectors.fbcustomaudience;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.BufferedObjectSink;
import io.castled.apps.connectors.fbcustomaudience.client.FbRestClient;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbAudienceUserFields;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.models.Field;
import io.castled.schema.models.Message;

import java.util.*;
import java.util.stream.Collectors;

public class FbCustomAudienceCustomerSink extends BufferedObjectSink<Message> {

    //  /{audience_id}/users API Limits
    private static final long BATCH_SIZE_MAX = 10000;

    private final FbCustomAudAppSyncConfig syncConfig;
    private final FbRestClient fbRestClient;
    private final ErrorOutputStream errorOutputStream;
    private final AppSyncStats syncStats;
    // display name -> schema name
    private final Map<String, String> nameMap;
    private final FbErrorParser errorParser;

    public FbCustomAudienceCustomerSink(FbAppConfig appConfig, FbCustomAudAppSyncConfig syncConfig,
                                        ErrorOutputStream errorOutputStream) {
        this.syncConfig = syncConfig;
        this.fbRestClient = new FbRestClient(appConfig, syncConfig);
        this.syncStats = new AppSyncStats();
        this.errorOutputStream = errorOutputStream;
        this.nameMap = Arrays.stream(FbAudienceUserFields.values())
                .collect(Collectors.toMap(field -> field.getDisplayName(), field -> field.getName()));
        this.errorParser = ObjectRegistry.getInstance(FbErrorParser.class);
    }

    @Override
    protected void writeRecords(List<Message> msgs) {

        List<String> schema = getSchema(msgs);
        List<List<String>> data = getData(msgs);
        FbCustomerErrors errors = this.fbRestClient.addCustomerList(schema, data);

        for (Message msg : msgs) {
            String errorMsg = errors.invalidEntrySamples.get(getRowKey(msg));
            if ( errorMsg != null) {
                this.errorOutputStream.writeFailedRecord(msg, errorParser.getPipelineError(errorMsg));
            }
        }
        updateStats(msgs.size(), Iterables.getLast(msgs).getOffset());
    }

    protected String getRowKey(Message msg) {
        // Expected format ["val1","val2","val3",...]
        String rowKey = "[";
        for (Field field : msg.getRecord().getFields()) {
            rowKey += String.format("\"%s\",", field.getValue());
        }
        // Remove trailing ","
        rowKey = rowKey.substring(0, rowKey.length() - 1);
        rowKey += "]";
        return rowKey;
    }

    protected  List<String> getSchema(List<Message> records) {
        Message msg = records.stream().findFirst().orElseThrow(() -> new CastledRuntimeException("Empty records list!"));
        return msg.getRecord().getFields().stream().map(field -> nameMap.get(field.getName())).collect(Collectors.toList());
    }

    protected List<List<String>> getData(List<Message> msgs) {
        List<List<String>> data = Lists.newArrayList();
        for (Message msg: msgs) {
            List<String> tuple = msg.getRecord().getFields().stream()
                    .map(field -> new AbstractMap.SimpleEntry<>(FbCustomAudienceFormatUtils.formatValue(
                            field.getValue(), field.getName()), field.getName()))
                    .map(val -> syncConfig.isHashingRequired()?
                            FbCustomAudienceFormatUtils.hashValue(val.getKey(), val.getValue()) :
                            val.getKey())
                    .collect(Collectors.toList());
            data.add(tuple);
        }
        return data;
    }

    private void updateStats(long processed, long maxOffset) {
        syncStats.setRecordsProcessed(syncStats.getRecordsProcessed() + processed);
        syncStats.setOffset(Math.max(syncStats.getOffset(), maxOffset));
    }

    public AppSyncStats getSyncStats() {
        return syncStats;
    }

    @Override
    public long getMaxBufferedObjects() {
        return BATCH_SIZE_MAX;
    }
}
