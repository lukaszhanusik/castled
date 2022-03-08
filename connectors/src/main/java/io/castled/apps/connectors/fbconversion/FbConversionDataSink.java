package io.castled.apps.connectors.fbconversion;

import io.castled.apps.DataSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class FbConversionDataSink implements DataSink  {

    private FbConversionBufferedSink fbConversionBufferedSink;

    @Override
    public void syncRecords(DataSinkRequest dataSinkRequest) throws Exception {

        this.fbConversionBufferedSink = new FbConversionBufferedSink((FbConversionAppConfig) dataSinkRequest.getExternalApp().getConfig(),
                (FbConversionSyncConfig) dataSinkRequest.getAppSyncConfig(), dataSinkRequest.getErrorOutputStream());

        DataSinkMessage msg;
        while ((msg = dataSinkRequest.getMessageInputStream().readMessage()) != null) {
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
