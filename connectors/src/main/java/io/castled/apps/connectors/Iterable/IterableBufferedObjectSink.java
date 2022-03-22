package io.castled.apps.connectors.Iterable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.castled.ObjectRegistry;
import io.castled.apps.BufferedObjectSink;
import io.castled.apps.connectors.Iterable.client.IterableSyncErrors;
import io.castled.apps.connectors.Iterable.client.IterableRestClient;
import io.castled.apps.connectors.Iterable.client.dtos.CatalogItemField;
import io.castled.apps.connectors.Iterable.client.dtos.EventField;
import io.castled.apps.connectors.Iterable.client.dtos.FieldConsts;
import io.castled.apps.connectors.Iterable.client.dtos.UserPrimaryKey;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.models.Field;
import io.castled.schema.models.Tuple;
import io.castled.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IterableBufferedObjectSink extends BufferedObjectSink<DataSinkMessage> {

    private static long BATCH_SIZE = 1000;
    private final IterableSyncConfig iterableSyncConfig;
    private final IterableRestClient iterableRestClient;
    private final AppSyncStats appSyncStats;
    private final ErrorOutputStream errorOutputStream;

    IterableBufferedObjectSink(IterableAppConfig iterableAppConfig, IterableSyncConfig iterableSyncConfig,
                               ErrorOutputStream errorOutputStream) {
        this.iterableSyncConfig = iterableSyncConfig;
        this.iterableRestClient = new IterableRestClient(iterableAppConfig);
        this.appSyncStats = new AppSyncStats();
        this.errorOutputStream = errorOutputStream;
    }

    @Override
    protected void writeRecords(List<DataSinkMessage> msgs) {

        IterableSyncErrors syncErrors;

        switch (IterableSchemaUtils.getIterableObject(iterableSyncConfig.getObject())) {
            case USERS:
                syncErrors = this.iterableRestClient.bulkUserUpdate(getUserData(msgs));
                processErrors(syncErrors, msgs);
                break;
            case EVENTS:
                this.iterableRestClient.bulkEventUpdate(getEventData(msgs));
                break;
            case CATALOGS:
                this.iterableRestClient.bulkCatalogItemsUpdate(iterableSyncConfig.getCatalogName(), getCatalogData(msgs));
                break;
            default:
                throw new CastledRuntimeException("Not implemented!");
        }
        updateStats(msgs.size(), Iterables.getLast(msgs).getOffset());
    }

    public void processErrors(IterableSyncErrors syncErrors, List<DataSinkMessage> msgs) {
        IterableErrorParser errorParser = ObjectRegistry.getInstance(IterableErrorParser.class);

        if (syncErrors.getFailedEmails().size() > 0) {
            Map<String, DataSinkMessage> emailMsgMap = msgs.stream()
                    .collect(Collectors.toMap(msg -> (String) msg.getRecord().getValue(UserPrimaryKey.EMAIL.getName()), Function.identity()));

            syncErrors.getFailedEmails().stream().forEach(email ->
                    this.errorOutputStream.writeFailedRecord(emailMsgMap.get(email), errorParser.getPipelineError(UserPrimaryKey.EMAIL.getName())));
        }

        if (syncErrors.getFailedUserIds().size() > 0) {
            Map<String, DataSinkMessage> userIdMsgMap = msgs.stream()
                    .collect(Collectors.toMap(msg -> (String) msg.getRecord().getValue(UserPrimaryKey.USER_ID.getName()), Function.identity()));

            syncErrors.getFailedEmails().stream().forEach(email ->
                    this.errorOutputStream.writeFailedRecord(userIdMsgMap.get(email), errorParser.getPipelineError(UserPrimaryKey.USER_ID.getName())));
        }
    }

    @Override
    public long getMaxBufferedObjects() {
        return BATCH_SIZE;
    }

    List<Map<String, Object>> getUserData(List<DataSinkMessage> msgs) {

        List<Map<String, Object>> users = Lists.newArrayList();
        for (DataSinkMessage msg : msgs) {
            Tuple record = msg.getRecord();
            Map<String, Object> userInfo = Maps.newHashMap();
            Map<String, Object> dataFields = Maps.newHashMap();

            for (Field field : record.getFields()) {
                if (IterableSchemaUtils.isUserPrimaryKey(field.getName())) {
                    userInfo.put(field.getName(), field.getValue());
                } else {
                    dataFields.put(field.getName(), field.getValue());
                }
            }

            if (!dataFields.isEmpty()) {
                userInfo.put(FieldConsts.DATA_FIELDS, dataFields);
            }
            userInfo.put(FieldConsts.MERGE_NESTED_OBJECTS, true);
            userInfo.put(FieldConsts.PREFER_USER_ID, true);
            users.add(userInfo);
        }
        return users;
    }

    Map<String, Map<String, Object>> getCatalogData(List<DataSinkMessage> msgs) {

        Map<String, Map<String, Object>> catalog = Maps.newHashMap();
        for (DataSinkMessage msg : msgs) {
            Tuple record = msg.getRecord();
            Map<String, Object> item = Maps.newHashMap();

            for (Field field : record.getFields()) {
                if (!IterableSchemaUtils.isCatalogPrimaryKey(field.getName())) {
                    item.put(field.getName(), field.getValue());
                }
            }
            String itemId = (String) msg.getRecord().getValue(CatalogItemField.ITEM_ID.getName());
            IterableValidationUtils.validateValue(itemId, CatalogItemField.ITEM_ID.getName());
            catalog.put(itemId, item);
        }
        return catalog;
    }

    List<Map<String, Object>> getEventData(List<DataSinkMessage> msgs) {

        List<Map<String, Object>> events = Lists.newArrayList();
        for (DataSinkMessage msg : msgs) {
            Tuple record = msg.getRecord();
            Map<String, Object> eventInfo = Maps.newHashMap();
            Map<String, Object> dataFields = Maps.newHashMap();

            for (Field field : record.getFields()) {
                if (IterableSchemaUtils.isCustomEventField(field.getName())) {
                    dataFields.put(field.getName(), field.getValue());
                } else {
                    eventInfo.put(field.getName(), field.getValue());
                }
            }

            // Set custom fields
            if (!dataFields.isEmpty()) {
                eventInfo.put(FieldConsts.DATA_FIELDS, dataFields);
            }

            // Set campaignId if not mapped
            if (!eventInfo.containsKey(EventField.CAMPAIGN_ID.getName()) &&
                    !StringUtils.isEmpty(iterableSyncConfig.getCampaignId())) {
                eventInfo.put(EventField.CAMPAIGN_ID.getName(), Integer.parseInt(iterableSyncConfig.getCampaignId()));
            }

            // Set templateId if not mapped
            if (!eventInfo.containsKey(EventField.TEMPLATE_ID.getName()) &&
                    !StringUtils.isEmpty(iterableSyncConfig.getTemplateId())) {
                eventInfo.put(EventField.TEMPLATE_ID.getName(), Integer.parseInt(iterableSyncConfig.getTemplateId()));
            }
            events.add(eventInfo);
        }
        return events;
    }

    private void updateStats(long processed, long maxOffset) {
        appSyncStats.setRecordsProcessed(appSyncStats.getRecordsProcessed() + processed);
        appSyncStats.setOffset(Math.max(appSyncStats.getOffset(), maxOffset));
    }

    public AppSyncStats getSyncStats() {
        return appSyncStats;
    }
}
