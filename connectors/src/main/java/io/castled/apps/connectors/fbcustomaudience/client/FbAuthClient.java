package io.castled.apps.connectors.fbcustomaudience.client;

import com.google.inject.Inject;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbTokenResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class FbAuthClient {

    private static final String TOKEN_SERVICE_API_END_POINT = "https://graph.facebook.com/v13.0/oauth/access_token";

    private final Client client;

    @Inject
    public FbAuthClient(Client client) {
        this.client = client;
    }

    public FbTokenResponse getTokenViaAuthorizationCode(String authorizationCode, String clientId,
                                                        String clientSecret, String redirectUri) {
        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(TOKEN_SERVICE_API_END_POINT)
                    .queryParam("code", authorizationCode)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", redirectUri)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        });
        return response.readEntity(FbTokenResponse.class);
    }

    public FbTokenResponse renewToken(String accessToken, String clientId, String clientSecret) {
        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(TOKEN_SERVICE_API_END_POINT)
                    .queryParam("fb_exchange_token", accessToken)
                    .queryParam("grant_type", "fb_exchange_token")
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        });
        return response.readEntity(FbTokenResponse.class);
    }
}
