package io.castled.apps.connectors.fbconversion.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.castled.ObjectRegistry;
import io.castled.apps.connectors.fbconversion.FbConversionAppConfig;
import io.castled.apps.connectors.fbconversion.FbConversionStatus;
import io.castled.apps.connectors.fbconversion.FbConversionSyncConfig;
import io.castled.apps.connectors.fbconversion.client.dtos.ConversionEventErrorResponse;
import io.castled.apps.connectors.fbconversion.client.dtos.ConversionEventResponse;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.utils.ResponseUtils;
import io.castled.utils.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class FbConversionClient {

    private static final String API_ENDPOINT = "https://graph.facebook.com/v13.0";

    private final Client client;
    private final FbConversionAppConfig appConfig;
    private final FbConversionSyncConfig appSyncConfig;

    public FbConversionClient(FbConversionAppConfig appConfig, FbConversionSyncConfig appSyncConfig) {
        this.client = ObjectRegistry.getInstance(Client.class);
        this.appConfig = appConfig;
        this.appSyncConfig = appSyncConfig;
    }

    public FbConversionStatus sendConversionEvents(List<Map<String, Object>> data) {
        String pixelID = this.appConfig.getPixelId();
        ObjectMapper objMapper =  new ObjectMapper();

        Form form = new Form();
        try {
            form.param("access_token", this.appConfig.getAccessToken());
            form.param("data", objMapper.writeValueAsString(data));
            if (!StringUtils.isEmpty(this.appSyncConfig.getTestEventCode())) {
                form.param("test_event_code", appSyncConfig.getTestEventCode());
            }
        } catch (JsonProcessingException e) {
            throw new CastledRuntimeException(e);
        }

        Response response = this.client.target(String.format("%s/%s/events", API_ENDPOINT, pixelID))
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(form));

        if (!ResponseUtils.is2xx(response)) {
            ConversionEventErrorResponse error = response.readEntity(ConversionEventErrorResponse.class);
            if (error.getError().getCode() == 100) {
                // Invalid param error.
                return new FbConversionStatus(0, error.getError().getErrorUserTitle(),
                        error.getError().getErrorUserMsg());
            } else {
                // Fail the sync job.
                throw new CastledRuntimeException(String.format("%s:%s", error.getError().getType(),
                        error.getError().getMessage()));
            }
        } else {
            ConversionEventResponse listResponse = response.readEntity(ConversionEventResponse.class);
            return new FbConversionStatus(listResponse.getEventsReceived(), null, null);
        }
    }
}
