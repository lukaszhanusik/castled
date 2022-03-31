package io.castled.apps.connectors.fbconversion;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class FbConversionDataWriter implements DataWriter {

    private FbConversionBufferedSink fbConversionBufferedSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        this.fbConversionBufferedSink = new FbConversionBufferedSink((FbConversionAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                (FbConversionSyncConfig) dataWriteRequest.getAppSyncConfig(), dataWriteRequest.getErrorOutputStream());

        DataSinkMessage msg;
        while ((msg = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.fbConversionBufferedSink.writeRecord(msg);
        }
        this.fbConversionBufferedSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(fbConversionBufferedSink).map(sinkRef -> sinkRef.getSyncStats())
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
