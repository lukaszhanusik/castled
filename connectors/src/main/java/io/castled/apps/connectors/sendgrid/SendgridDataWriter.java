package io.castled.apps.connectors.sendgrid;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class SendgridDataWriter implements DataWriter {

    private volatile SendgridContactSink sendgridContactSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        this.sendgridContactSink = new SendgridContactSink((SendgridAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                (SendgridAppSyncConfig) dataWriteRequest.getAppSyncConfig(), dataWriteRequest.getErrorOutputStream());
        DataSinkMessage msg;
        while ((msg = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.sendgridContactSink.writeRecord(msg);
        }
        this.sendgridContactSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(sendgridContactSink).map(SendgridContactSink::getSyncStats)
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
