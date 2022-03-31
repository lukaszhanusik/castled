package io.castled.apps.connectors.activecampaign;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;

import java.util.Optional;

public class ActiveCampaignDataWriter implements DataWriter {

    private ActiveCampaignAudienceSink activeCampaignAudienceSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        DataSinkMessage message;
        this.activeCampaignAudienceSink = new ActiveCampaignAudienceSink((ActiveCampaignAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                dataWriteRequest.getErrorOutputStream(), ((ActiveCampaignAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject());
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.activeCampaignAudienceSink.writeRecord(message);
        }
        this.activeCampaignAudienceSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(this.activeCampaignAudienceSink)
                .map(audienceSinkRef -> this.activeCampaignAudienceSink.getSyncStats())
                .map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0))
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
