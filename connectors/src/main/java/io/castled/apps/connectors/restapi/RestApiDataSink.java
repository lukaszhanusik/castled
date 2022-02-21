package io.castled.apps.connectors.restapi;

import io.castled.apps.DataSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.schema.models.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class RestApiDataSink implements DataSink {

    private volatile RestApiObjectSync restApiObjectSink;

    @Override
    public void syncRecords(DataSinkRequest dataSinkRequest) throws Exception {
        this.restApiObjectSink = new RestApiObjectSync(dataSinkRequest);
        Message message;
        while ((message = dataSinkRequest.getMessageInputStream().readMessage()) != null) {
            this.restApiObjectSink.writeRecord(message);
        }
        this.restApiObjectSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(this.restApiObjectSink)
                .map(audienceSinkRef -> this.restApiObjectSink.getSyncStats())
                .map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0))
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
