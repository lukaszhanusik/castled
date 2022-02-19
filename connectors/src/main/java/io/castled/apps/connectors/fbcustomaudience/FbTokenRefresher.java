package io.castled.apps.connectors.fbcustomaudience;

import io.castled.ObjectRegistry;
import io.castled.apps.connectors.fbcustomaudience.client.FbAuthClient;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbTokenResponse;
import io.castled.cache.OAuthCache;
import io.castled.oauth.AccessTokenRefresher;
import io.castled.oauth.OAuthClientConfig;
import io.castled.oauth.OAuthDAO;
import io.castled.oauth.OAuthDetails;
import io.castled.pubsub.MessagePublisher;
import io.castled.pubsub.registry.OAuthDetailsUpdatedMessage;
import org.jdbi.v3.core.Jdbi;

import java.time.Instant;

public class FbTokenRefresher implements AccessTokenRefresher<FbAccessConfig>  {

    private final OAuthDAO oAuthDAO;
    private final FbAuthClient fbAuthClient;
    private final OAuthClientConfig oAuthClientConfig;
    private final OAuthCache oAuthCache;
    private final MessagePublisher messagePublisher;

    FbTokenRefresher(OAuthClientConfig oAuthClientConfig) {

        this.oAuthDAO = ObjectRegistry.getInstance(Jdbi.class).onDemand(OAuthDAO.class);
        this.fbAuthClient = ObjectRegistry.getInstance(FbAuthClient.class);
        this.oAuthClientConfig = oAuthClientConfig;
        this.oAuthCache = ObjectRegistry.getInstance(OAuthCache.class);
        this.messagePublisher = ObjectRegistry.getInstance(MessagePublisher.class);
    }

    public FbAccessConfig refreshAndPersistAccessConfig(Long oAuthToken) {
        OAuthDetails oAuthDetails = this.oAuthCache.getValue(oAuthToken);
        FbTokenResponse fbTokenResponse = this.fbAuthClient.renewToken(oAuthDetails.getAccessConfig().getAccessToken(),
                oAuthClientConfig.getClientId(), oAuthClientConfig.getClientSecret());
        FbAccessConfig fbAccessConfig = FbAccessConfig.builder()
                .accessToken(fbTokenResponse.getAccessToken())
                .expiresIn(fbTokenResponse.getExpiresIn())
                .tokenEpochSecond(Instant.now().getEpochSecond())
                .build();
        this.oAuthDAO.updateAccessConfig(oAuthToken, fbAccessConfig);
        this.messagePublisher.publishMessage(new OAuthDetailsUpdatedMessage(oAuthToken));
        return fbAccessConfig;
    }
}
