package io.castled.apps.connectors.googleads;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v7.enums.ConversionActionTypeEnum;
import com.google.ads.googleads.v7.enums.CustomerMatchUploadKeyTypeEnum;
import com.google.ads.googleads.v7.resources.ConversionAction;
import com.google.ads.googleads.v7.services.GoogleAdsRow;
import com.google.ads.googleads.v7.services.GoogleAdsServiceClient;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import io.castled.apps.ExternalApp;
import io.castled.apps.OAuthAppConfig;
import io.castled.apps.dtos.AppSyncConfigDTO;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.oauth.OAuthDetails;
import io.castled.optionsfetchers.appsync.AppSyncOptionsFetcher;
import io.castled.services.OAuthService;

import java.util.List;
import java.util.stream.Collectors;

public class GoogleAdsSubResourceFetcher implements AppSyncOptionsFetcher {

    private final OAuthService oAuthService;

    @Inject
    public GoogleAdsSubResourceFetcher(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public List<FormFieldOption> getOptions(AppSyncConfigDTO config, ExternalApp externalApp) {

        GoogleAdsAppSyncConfig mappingConfig = (GoogleAdsAppSyncConfig) config.getAppSyncConfig();
        GoogleAdsAppConfig googleAdsAppConfig = (GoogleAdsAppConfig) externalApp.getConfig();
        OAuthDetails oAuthDetails = this.oAuthService.getOAuthDetails(googleAdsAppConfig.getOAuthToken());
        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(
                GoogleAdUtils.getClientProperties(googleAdsAppConfig, oAuthDetails.getAccessConfig().getRefreshToken(),
                        mappingConfig.getLoginCustomerId())).build();

        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            switch (mappingConfig.getObjectType()) {
                case CUSTOMER_MATCH:
                    return getCustomerMatchObjects((GoogleAdsAppConfig) externalApp.getConfig(), googleAdsServiceClient, mappingConfig);
                case CLICK_CONVERSIONS:
                case CALL_CONVERSIONS:
                    return getConversionObjects(googleAdsServiceClient, mappingConfig, mappingConfig.getObjectType());
                default:
                    throw new CastledRuntimeException(String.format("Invalid google ads object type %s", mappingConfig.getObjectType()));
            }
        }
    }


    private List<FormFieldOption> getConversionObjects(GoogleAdsServiceClient googleAdsServiceClient,
                                                       GoogleAdsAppSyncConfig mappingConfig, GAdsObjectType gAdsObjectType) {
        GoogleAdsServiceClient.SearchPagedResponse searchPagedResponse = googleAdsServiceClient
                .search(String.valueOf(mappingConfig.getAccountId()),
                        "SELECT conversion_action.name, conversion_action.resource_name,conversion_action.type FROM conversion_action");
        List<GadsConversion> conversionActionObjects = Lists.newArrayList();
        for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
            ConversionAction conversionAction = googleAdsRow.getConversionAction();
            if (conversionAction.getType() == ConversionActionTypeEnum.ConversionActionType.UPLOAD_CALLS &&
                    gAdsObjectType == GAdsObjectType.CALL_CONVERSIONS) {
                conversionActionObjects.add(new GadsConversion(conversionAction.getName(), conversionAction.getResourceName()));
            }
            if (conversionAction.getType() == ConversionActionTypeEnum.ConversionActionType.UPLOAD_CLICKS &&
                    gAdsObjectType == GAdsObjectType.CLICK_CONVERSIONS) {
                conversionActionObjects.add(new GadsConversion(conversionAction.getName(), conversionAction.getResourceName()));
            }
        }
        return conversionActionObjects.stream().map(conversionActionObject -> new FormFieldOption(conversionActionObject, conversionActionObject.getConversion()))
                .collect(Collectors.toList());
    }


    private List<FormFieldOption> getCustomerMatchObjects(OAuthAppConfig googleAdsAppConfig, GoogleAdsServiceClient
            googleAdsServiceClient, GoogleAdsAppSyncConfig mappingConfig) {
        GoogleAdsServiceClient.SearchPagedResponse searchPagedResponse = googleAdsServiceClient
                .search(String.valueOf(mappingConfig.getAccountId()),
                        "SELECT user_list.name, user_list.id, user_list.resource_name," +
                                "user_list.crm_based_user_list.upload_key_type FROM user_list");

        List<GadsCustomerMatch> userLists = Lists.newArrayList();
        for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
            CustomerMatchType customerMatchType =
                    getCustomerMatchType(googleAdsRow.getUserList().getCrmBasedUserList().getUploadKeyType());
            if (customerMatchType != null && customerMatchType == mappingConfig.getCustomerMatchType()) {
                userLists.add(new GadsCustomerMatch(googleAdsRow.getUserList().getName(),
                        googleAdsRow.getUserList().getResourceName()));
            }
        }
        return userLists.stream().map(userList -> new FormFieldOption(userList, userList.getCustomerMatchList()))
                .collect(Collectors.toList());
    }

    private CustomerMatchType getCustomerMatchType(CustomerMatchUploadKeyTypeEnum.CustomerMatchUploadKeyType
                                                           uploadKeyType) {
        switch (uploadKeyType) {
            case CONTACT_INFO:
                return CustomerMatchType.CONTACT_INFO;
            case MOBILE_ADVERTISING_ID:
                return CustomerMatchType.MOBILE_ADVERTISING_ID;
            case CRM_ID:
                return CustomerMatchType.CRM_ID;
            default:
                throw new CastledRuntimeException(String.format("Invalid upload key type %s", uploadKeyType));
        }
    }

}
