package io.castled.apps.connectors.intercom;

import com.google.inject.Inject;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.apps.models.GenericSyncObject;
import io.castled.apps.models.PrimaryKeyIdMapper;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.models.AppSyncMode;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class IntercomDataWriter implements DataWriter {

    private final Map<IntercomObject, IntercomObjectSink> intercomObjectSinks;

    private long skippedRecords = 0;

    private volatile IntercomObjectSink intercomObjectSink;

    @Inject
    public IntercomDataWriter(Map<IntercomObject, IntercomObjectSink> intercomObjectSinks) {
        this.intercomObjectSinks = intercomObjectSinks;
    }

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        GenericSyncObject intercomSyncObject = ((IntercomAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject();
        IntercomObject intercomObject = IntercomObject.getObjectByName(intercomSyncObject.getObjectName());
        this.intercomObjectSink =
                this.intercomObjectSinks.get(intercomObject).initialize(intercomObject, dataWriteRequest.getAppSyncConfig(),
                        (IntercomAppConfig) dataWriteRequest.getExternalApp().getConfig(), dataWriteRequest.getErrorOutputStream(),
                        dataWriteRequest.getPrimaryKeys());
        DataSinkMessage message;
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            if (!this.writeRecord(message, dataWriteRequest.getAppSyncConfig(), intercomObjectSink,
                    dataWriteRequest.getPrimaryKeys())) {
                skippedRecords++;
            }
        }
        intercomObjectSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(intercomObjectSink).map(IntercomObjectSink::getSyncStats).
                map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(),
                        statsRef.getOffset(), skippedRecords)).orElse(new AppSyncStats(0, 0, 0));
    }

    private boolean writeRecord(DataSinkMessage message, AppSyncConfig appSyncConfig,
                                IntercomObjectSink intercomObjectSink, List<String> primaryKeys) {

        List<Object> primaryKeyValues = primaryKeys.stream().map(pk -> message.getRecord().getValue(pk)).collect(Collectors.toList());

        PrimaryKeyIdMapper primaryKeyIdMapper = intercomObjectSink.getPrimaryKeyIdMapper();
        Object objectId = primaryKeyIdMapper.getObjectId(primaryKeyValues);

        IntercomAppSyncConfig intercomSyncConfig = (IntercomAppSyncConfig) appSyncConfig;
        if (intercomSyncConfig.getMode() == AppSyncMode.UPDATE && objectId == null) {
            return false;
        }
        if (objectId == null) {
            intercomObjectSink.createObject(message);
        } else {
            intercomObjectSink.updateObject(objectId, message);
        }
        return true;
    }
}
