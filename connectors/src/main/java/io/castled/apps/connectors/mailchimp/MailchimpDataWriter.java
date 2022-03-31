package io.castled.apps.connectors.mailchimp;

import io.castled.apps.DataWriter;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class MailchimpDataWriter implements DataWriter {

    private MailchimpAudienceSink mailchimpAudienceSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        DataSinkMessage message;
        this.mailchimpAudienceSink = new MailchimpAudienceSink((OAuthAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                dataWriteRequest.getErrorOutputStream(), ((MailchimpAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject());
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.mailchimpAudienceSink.writeRecord(message);
        }
        this.mailchimpAudienceSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(this.mailchimpAudienceSink)
                .map(audienceSinkRef -> this.mailchimpAudienceSink.getSyncStats())
                .map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0))
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
