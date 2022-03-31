package io.castled.apps.connectors.fbcustomaudience;

import io.castled.ObjectRegistry;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.oauth.OAuthDetails;
import io.castled.services.OAuthService;

import java.time.Instant;
import java.util.Optional;

public class FbCustomAudDataWriter implements DataWriter {

    private volatile FbCustomAudienceCustomerSink fbCustomAudienceCustomerSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        renewAccessToken((FbAppConfig) dataWriteRequest.getExternalApp().getConfig());
        fbCustomAudienceCustomerSink = new FbCustomAudienceCustomerSink((FbAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                (FbCustomAudAppSyncConfig) dataWriteRequest.getAppSyncConfig(), dataWriteRequest.getErrorOutputStream());

        DataSinkMessage msg;
        while ((msg = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            this.fbCustomAudienceCustomerSink.writeRecord(msg);
        }
        this.fbCustomAudienceCustomerSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(fbCustomAudienceCustomerSink).map(FbCustomAudienceCustomerSink::getSyncStats)
                .orElse(new AppSyncStats(0, 0, 0));
    }

    // Check once before the start of each pipeline run to see if the access token needs to be renewed.
    private void renewAccessToken(FbAppConfig appConfig) {
        OAuthDetails oAuthDetails = ObjectRegistry.getInstance(OAuthService.class).getOAuthDetails(appConfig.getOAuthToken());
        FbAccessConfig accessConfig = (FbAccessConfig) oAuthDetails.getAccessConfig();
        // Refresh access token if only less than 10 days until expiry.
        // These are long-lived token with typical expiry period of 60 days.
        long TOKEN_REFRESH_BUFFER_SECONDS = 10 * 24 * 3600;
        if ((accessConfig.getTokenEpochSecond() + accessConfig.getExpiresIn() - TOKEN_REFRESH_BUFFER_SECONDS)
                < Instant.EPOCH.getEpochSecond()) {
            FbTokenRefresher fbTokenRefresher = new FbTokenRefresher(appConfig.getClientConfig());
            fbTokenRefresher.refreshAndPersistAccessConfig(appConfig.getOAuthToken());
        }
    }
}
