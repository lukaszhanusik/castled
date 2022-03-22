package io.castled.apps.connectors.Iterable.client;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.apps.connectors.Iterable.IterableAppConfig;
import io.castled.apps.connectors.Iterable.client.dtos.*;
import io.castled.core.WaitTimeAndRetry;
import io.castled.exceptions.CastledException;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.functionalinterfaces.ThrowingSupplier;
import io.castled.utils.ResponseUtils;
import io.castled.utils.RetryUtils;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class IterableRestClient {

    private static final String API_ENDPOINT = "https://api.iterable.com/api";

    private final Client client;
    private final IterableAppConfig appConfig;

    public IterableRestClient(IterableAppConfig appConfig) {
        this.client = ObjectRegistry.getInstance(Client.class);
        this.appConfig = appConfig;
    }

    public Map<String, String> getUserFields() {
        Response response = executeRequest(() -> {
            return this.client.target(String.format("%s/%s", API_ENDPOINT, "users/getFields"))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .get();
        });
        UserFieldsResponse userFieldsResponse = response.readEntity(UserFieldsResponse.class);
        return userFieldsResponse.getFields();
    }

    public Map<String, String> getCatalogFieldMappings(String catalogName) {
        Response response = executeRequest(() -> {
            return this.client.target(String.format("%s/catalogs/%s/fieldMappings", API_ENDPOINT, catalogName))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .get();
        });
        CatalogFieldMappingResponse fieldMappingResponse = response.readEntity(CatalogFieldMappingResponse.class);
        return fieldMappingResponse.getParams().getDefinedMappings();
    }

    public IterableSyncErrors bulkUserUpdate(List<Map<String, Object>> data) {
        BulkUserUpdateRequest bulkUserUpdateRequest = new BulkUserUpdateRequest(data);
        BulkUserUpdateResponse bulkUserUpdateResponse = executeAndRetryRequest(() -> {
           Response response =  this.client.target(String.format("%s/users/bulkUpdate", API_ENDPOINT))
                   .request(MediaType.APPLICATION_JSON)
                   .header("api-key", appConfig.getApiKey())
                   .post(Entity.json(bulkUserUpdateRequest));
           if (!ResponseUtils.is2xx(response)) {
               ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
               if (response.getStatus() == 429) {
                   throw new ApiLimitExceededException("Iterable Api limit hit!");
               } else {
                   throw new CastledException(String.format("%s:%s", errorResponse.getCode(), errorResponse.getMsg()));
               }
           } else {
               return response.readEntity(BulkUserUpdateResponse.class);
           }
        });
        IterableSyncErrors syncErrors = new IterableSyncErrors(bulkUserUpdateResponse.getInvalidEmails(),
                bulkUserUpdateResponse.getInvalidUserIds());
        return syncErrors;
    }

    public IterableSyncErrors bulkEventUpdate(List<Map<String, Object>> data) {
        BulkEventUpdateRequest bulkEventUpdateRequest = new BulkEventUpdateRequest(data);
        BulkEventUpdateResponse bulkEventUpdateResponse = executeAndRetryRequest(() -> {
            Response response = this.client.target(String.format("%s/events/trackBulk", API_ENDPOINT))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .post(Entity.json(bulkEventUpdateRequest));
            if (!ResponseUtils.is2xx(response)) {
                ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
                if (response.getStatus() == 429) {
                    throw new ApiLimitExceededException("Iterable Api limit hit!");
                } else {
                    throw new CastledException(String.format("%s:%s", errorResponse.getCode(), errorResponse.getMsg()));
                }
            } else {
                return response.readEntity(BulkEventUpdateResponse.class);
            }
        });
        IterableSyncErrors syncErrors = new IterableSyncErrors(bulkEventUpdateResponse.getInvalidEmails(),
                bulkEventUpdateResponse.getInvalidUserIds());
        return syncErrors;
    }

    public void bulkCatalogItemsUpdate(String catalogName, Map<String, Map<String, Object>> data) {
        BulkCatalogUpdateRequest bulkCatalogUpdateRequest = new BulkCatalogUpdateRequest(data, true);
        BulkCatalogUpdateResponse bulkCatalogUpdateResponse = executeAndRetryRequest(() -> {
            Response response = this.client.target(String.format("%s/catalogs/%s/items", API_ENDPOINT, catalogName))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .post(Entity.json(bulkCatalogUpdateRequest));
            if (!ResponseUtils.is2xx(response)) {
                if (response.getStatus() == 429) {
                    response.close();
                    throw new ApiLimitExceededException("Iterable Api limit hit!");
                } else if (response.getStatus() == 400){
                    ErrorResponse400 errorResponse = response.readEntity(ErrorResponse400.class);
                    throw new CastledRuntimeException(String.format("%s:%s", errorResponse.getError(), errorResponse.getMessage()));
                } else {
                    ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
                    throw new CastledRuntimeException(String.format("%s:%s", errorResponse.getCode(), errorResponse.getMsg()));
                }
            } else {
                return response.readEntity(BulkCatalogUpdateResponse.class);
            }
        });
    }

    public List<CampaignId> getCampaigns() {
        Response response = executeRequest(() -> {
            return  this.client.target(String.format("%s/%s", API_ENDPOINT, "campaigns"))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .get();
        });
        CampaignIdResponse campaignIdResponse = response.readEntity(CampaignIdResponse.class);
        return campaignIdResponse.getCampaigns();
    }

    public List<TemplateId> getTemplates() {
        Response response = executeRequest(() -> {
            return this.client.target(String.format("%s/%s", API_ENDPOINT, "templates"))
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .get();
        });
        TemplateIdResponse templateIdResponse = response.readEntity(TemplateIdResponse.class);
        return templateIdResponse.getTemplates();
    }

    public List<CatalogName> getCatalogs() {
        Response response = executeRequest(() -> {
            return this.client.target(String.format("%s/%s", API_ENDPOINT, "catalogs"))
                    .queryParam("pageSize", 100000)
                    .request(MediaType.APPLICATION_JSON)
                    .header("api-key", appConfig.getApiKey())
                    .get();
        });
        CatalogResponse catalogResponse = response.readEntity(CatalogResponse.class);
        return catalogResponse.getParams().getCatalogNames();
    }

    private <T> T executeAndRetryRequest(ThrowingSupplier<T> supplier) {
        try {
            return RetryUtils.retrySupplier(supplier, 5, Lists.newArrayList(ApiLimitExceededException.class),
                    ((throwable, attempts) -> {
                        return new WaitTimeAndRetry(200, true);
                    }));
        } catch (Exception e) {
            throw new CastledRuntimeException(e);
        }
    }

    public static Response executeRequest(Supplier<Response> supplier) {
        Response response = supplier.get();
        if (!ResponseUtils.is2xx(response)) {
            ErrorResponse error = response.readEntity(ErrorResponse.class);
            throw new CastledRuntimeException(String.format("%s:%s", error.getCode(), error.getMsg()));
        } else {
            return response;
        }
    }
}
