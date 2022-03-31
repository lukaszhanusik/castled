package io.castled.apps.connectors.restapi;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class RestApiDataWriter implements DataWriter {

    private volatile RestApiObjectSync restApiObjectSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        this.restApiObjectSink = new RestApiObjectSync(dataWriteRequest);
        DataSinkMessage message;
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
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
