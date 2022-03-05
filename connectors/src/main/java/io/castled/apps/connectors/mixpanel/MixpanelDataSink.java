package io.castled.apps.connectors.mixpanel;

import io.castled.apps.DataSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class MixpanelDataSink implements DataSink {

    private volatile MixpanelObjectSink<DataSinkMessage> mixedPanelObjectSink;

    @Override
    public void syncRecords(DataSinkRequest dataSinkRequest) throws Exception {

        this.mixedPanelObjectSink = getObjectSink(dataSinkRequest);
        log.info("Sync started for mix panel");
        DataSinkMessage message;
        while ((message = dataSinkRequest.getMessageInputStream().readMessage()) != null) {
            this.mixedPanelObjectSink.writeRecord(message);
        }
        this.mixedPanelObjectSink.flushRecords();
    }

    private MixpanelObjectSink<DataSinkMessage> getObjectSink(DataSinkRequest dataSinkRequest) {
        MixpanelObjectSink<DataSinkMessage> bufferedObjectSink = null;
        MixpanelObject mixpanelObject = MixpanelObject
                .getObjectByName(((MixpanelAppSyncConfig)dataSinkRequest.getAppSyncConfig()).getObject().getObjectName());
        switch (mixpanelObject) {
            case USER_PROFILE:
                bufferedObjectSink = new MixpanelUserProfileSink(dataSinkRequest);
                break;
            case GROUP_PROFILE:
                bufferedObjectSink = new MixpanelGroupProfileSink(dataSinkRequest);
                break;
            case EVENT:
                bufferedObjectSink = new MixpanelEventSink(dataSinkRequest);
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
