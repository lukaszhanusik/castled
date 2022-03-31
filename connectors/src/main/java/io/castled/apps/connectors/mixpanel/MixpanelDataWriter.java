package io.castled.apps.connectors.mixpanel;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MixpanelDataWriter implements DataWriter {

    private volatile MixpanelObjectSink<DataSinkMessage> mixedPanelObjectSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        this.mixedPanelObjectSink = getObjectSink(dataWriteRequest);
        log.info("Sync started for mix panel");
        DataSinkMessage message;
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.mixedPanelObjectSink.writeRecord(message);
        }
        this.mixedPanelObjectSink.flushRecords();
    }

    private MixpanelObjectSink<DataSinkMessage> getObjectSink(DataWriteRequest dataWriteRequest) {
        MixpanelObjectSink<DataSinkMessage> bufferedObjectSink = null;
        MixpanelObject mixpanelObject = MixpanelObject
                .getObjectByName(((MixpanelAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject().getObjectName());
        switch (mixpanelObject) {
            case USER_PROFILE:
                bufferedObjectSink = new MixpanelUserProfileSink(dataWriteRequest);
                break;
            case GROUP_PROFILE:
                bufferedObjectSink = new MixpanelGroupProfileSink(dataWriteRequest);
                break;
            case EVENT:
                bufferedObjectSink = new MixpanelEventSink(dataWriteRequest);
                break;
            default:
                throw new CastledRuntimeException(String.format("Invalid object type %s!", mixpanelObject.getName()));
        }
        return bufferedObjectSink;
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(this.mixedPanelObjectSink)
                .map(audienceSinkRef -> this.mixedPanelObjectSink.getSyncStats())
                .map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0))
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
