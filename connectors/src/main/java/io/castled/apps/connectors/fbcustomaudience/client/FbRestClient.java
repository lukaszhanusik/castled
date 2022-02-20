package io.castled.apps.connectors.fbcustomaudience.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.castled.ObjectRegistry;
import io.castled.apps.connectors.fbcustomaudience.FbAccessConfig;
import io.castled.apps.connectors.fbcustomaudience.FbAppConfig;
import io.castled.apps.connectors.fbcustomaudience.FbCustomAudAppSyncConfig;
import io.castled.apps.connectors.fbcustomaudience.FbCustomerErrors;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.*;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.oauth.OAuthDetails;
import io.castled.services.OAuthService;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class FbRestClient {

    private static final String API_ENDPOINT = "https://graph.facebook.com/v13.0";
    private static final String USER_ID = "me";
    private static final long BATCH_SIZE = 10000;

    private final Client client;
    private final FbAppConfig appConfig;
    private final FbCustomAudAppSyncConfig appSyncConfig;
    private FbAccessConfig oauthAccessConfig;
    private OAuthDetails oAuthDetails;
    private SessionInfo sessionInfo;

    public FbRestClient(FbAppConfig appConfig, FbCustomAudAppSyncConfig appSyncConfig) {
        this.client = ObjectRegistry.getInstance(Client.class);
        this.appConfig = appConfig;
        this.appSyncConfig = appSyncConfig;
        this.oAuthDetails = ObjectRegistry.getInstance(OAuthService.class).getOAuthDetails(appConfig.getOAuthToken());
        this.oauthAccessConfig = (FbAccessConfig) this.oAuthDetails.getAccessConfig();
        this.sessionInfo = new SessionInfo(Instant.now().getEpochSecond(), 0, false, 0);
    }

    public List<AdAccount> getAllAdAccounts() {
        final String FIELD_NAME = "name";
        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(String.format("%s/%s/adaccounts", API_ENDPOINT, USER_ID))
                    .queryParam("fields", FIELD_NAME)
                    .queryParam("access_token", this.oAuthDetails.getAccessConfig().getAccessToken())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        });
        FbAdAccountResponse accountResponse = response.readEntity(FbAdAccountResponse.class);
        return accountResponse.getData();
    }

    public CustomAudienceResponse createCustomAudience(String audienceName) {
        CustomAudienceCreateRequest request = new CustomAudienceCreateRequest(audienceName, "CUSTOM", "",
                "USER_PROVIDED_ONLY", this.oAuthDetails.getAccessConfig().getAccessToken());
        Form form = new Form()
                .param("name", audienceName)
                .param("subtype", "CUSTOM")
                .param("description", "")
                .param("customer_file_source", "USER_PROVIDED_ONLY")
                .param("access_token", this.oAuthDetails.getAccessConfig().getAccessToken());

        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(String.format("%s/%s/customaudiences", API_ENDPOINT, appSyncConfig.getAccountId()))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.form(form));
        });
        CustomAudienceResponse audResponse = response.readEntity(CustomAudienceResponse.class);
        return audResponse;
    }

    public Optional<CustomAudienceResponse> getCustomAudience(String audienceName) {
        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(String.format("%s/%s/customaudiences", API_ENDPOINT, appSyncConfig.getAccountId()))
                    .queryParam("fields", "name")
                    .queryParam("access_token", this.oAuthDetails.getAccessConfig().getAccessToken())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        });
        CustomAudienceListResponse audResponse = response.readEntity(CustomAudienceListResponse.class);
        Optional<CustomAudienceResponse> audience = audResponse.getData().stream()
                .filter(aud -> audienceName.equals(aud.getName())).findFirst();
        return audience;
    }

    public String getOrCreateCustomAudienceId(String audienceName) {
        Optional<CustomAudienceResponse> audienceResponse = getCustomAudience(audienceName);
        CustomAudienceResponse audience;
        if (audienceResponse.isPresent()) {
            audience = audienceResponse.get();
        } else {
            audience = createCustomAudience(audienceName);
        }
        return audience.getId();
    }

    public FbCustomerErrors addCustomerList(List<String> schema, List<List<String>> data) {
        String customAudienceId = getOrCreateCustomAudienceId(appSyncConfig.getCustomAudienceName());
        CustomerListPayload payload = new CustomerListPayload(schema, data);
        ObjectMapper objMapper =  new ObjectMapper();
        this.sessionInfo.setBatchSeq(this.sessionInfo.getBatchSeq() + 1);
        this.sessionInfo.setEstimatedNumTotal(data.size());
        // This is only a best-effort check.
        this.sessionInfo.setLastBatchFlag(data.size() < BATCH_SIZE);

        Form form = new Form();
        try {
            form.param("access_token", this.oAuthDetails.getAccessConfig().getAccessToken());
            form.param("session", objMapper.writeValueAsString(this.sessionInfo));
            form.param("payload", objMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new CastledRuntimeException(e);
        }
        Response response = FbClientUtils.executeAndHandleError(() -> {
            return this.client.target(String.format("%s/%s/users", API_ENDPOINT, customAudienceId))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.form(form));
        });
        CustomerListResponse listResponse = response.readEntity(CustomerListResponse.class);
        return new FbCustomerErrors(listResponse.getNumInvalidEntries(), listResponse.getInvalidEntrySamples());
    }
}
