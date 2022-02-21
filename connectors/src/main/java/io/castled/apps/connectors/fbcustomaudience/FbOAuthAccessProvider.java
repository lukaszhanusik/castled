package io.castled.apps.connectors.fbcustomaudience;

import io.castled.ObjectRegistry;
import io.castled.apps.connectors.fbcustomaudience.client.FbAuthClient;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbTokenResponse;
import io.castled.oauth.BaseOauthAccessProvider;
import io.castled.oauth.OAuthAccessConfig;
import io.castled.oauth.OAuthClientConfig;

import java.time.Instant;

public class FbOAuthAccessProvider extends BaseOauthAccessProvider {

    private static final String FB_OAUTH_URL = "https://www.facebook.com/v13.0/dialog/oauth";
    private final FbAuthClient fbCustomAudOAuthClient;
    private final OAuthClientConfig oAuthClientConfig;

    public FbOAuthAccessProvider(OAuthClientConfig oAuthClientConfig) {
        this.fbCustomAudOAuthClient = ObjectRegistry.getInstance(FbAuthClient.class);
        this.oAuthClientConfig = oAuthClientConfig;
    }

    @Override
    public OAuthAccessConfig getAccessConfig(String authorizationCode, String redirectUri) {
        FbTokenResponse fbTokenResponse = this.fbCustomAudOAuthClient
                .getTokenViaAuthorizationCode(authorizationCode, oAuthClientConfig.getClientId(),
                        oAuthClientConfig.getClientSecret(), redirectUri);
        return FbAccessConfig.builder()
                .accessToken(fbTokenResponse.getAccessToken())
                .expiresIn(fbTokenResponse.getExpiresIn())
                .tokenEpochSecond(Instant.EPOCH.getEpochSecond())
                .build();
    }

    @Override
    public String getAuthorizationUrl(String stateId, String redirectUri, String clientId) {
        return String.format("%s?client_id=%s&redirect_uri=%s&state=%s&scope=ads_management",
                FB_OAUTH_URL, clientId, redirectUri, stateId);
    }
}
