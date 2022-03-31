package io.castled.apps.connectors.Iterable;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class IterableDataWriter implements DataWriter {

    private IterableBufferedObjectSink iterableBufferedObjectSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        this.iterableBufferedObjectSink = new IterableBufferedObjectSink((IterableAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                (IterableSyncConfig) dataWriteRequest.getAppSyncConfig(), dataWriteRequest.getErrorOutputStream());

        DataSinkMessage msg;
        while ((msg = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.iterableBufferedObjectSink.writeRecord(msg);
        }
        this.iterableBufferedObjectSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(iterableBufferedObjectSink).map(sinkRef -> sinkRef.getSyncStats())
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
