package io.castled.apps.connectors.googleads;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;


@GroupActivator(dependencies = {"accountId"}, group = GoogleAdsFormGroups.LOGIN_ACCOUNT_ID)
@GroupActivator(dependencies = {"loginCustomerId"}, group = GoogleAdsFormGroups.OBJECT_TYPE)
@GroupActivator(dependencies = {"objectType", "loginCustomerId"}, condition = "objectType == 'CUSTOMER_MATCH'",
        group = GoogleAdsFormGroups.CUSTOMER_MATCH_TYPE)

@GroupActivator(dependencies = {"customerMatchType, objectType"}, condition = "objectType == 'CUSTOMER_MATCH' && customerMatchType != null", group = GoogleAdsFormGroups.CUSTOMER_MATCH)
@GroupActivator(dependencies = {"objectType", "loginCustomerId"}, condition = "objectType == 'CLICK_CONVERSIONS'",
        group = GoogleAdsFormGroups.CLICK_CONVERSIONS)
@GroupActivator(dependencies = {"objectType", "loginCustomerId"}, condition = "objectType == 'CALL_CONVERSIONS'",
        group = GoogleAdsFormGroups.CALL_CONVERSIONS)

@GroupActivator(dependencies = {"objectType", "loginCustomerId"}, condition = "objectType == 'CALL_CONVERSIONS' || objectType == 'CLICK_CONVERSIONS'",
        group = GoogleAdsFormGroups.ZONE_ID)
@Getter
@Setter
public class GoogleAdsAppSyncConfig extends BaseAppSyncConfig {

    @FormField(type = FormFieldType.DROP_DOWN, title = "Customer Id", description = "Google customer Id from the Google ads console eg: 788-9993-09993",
            optionsRef = @OptionsRef(value = OptionsReferences.GADS_ACCOUNT_ID, type = OptionsRefType.DYNAMIC))
    private String accountId;

    @FormField(type = FormFieldType.DROP_DOWN, title = "Auto Generated Login CustomerId", optionsRef = @OptionsRef(value = OptionsReferences.GADS_LOGIN_ACCOUNT_ID, type = OptionsRefType.DYNAMIC), group = "loginAccountId")
    private String loginCustomerId;

    @FormField(type = FormFieldType.RADIO_GROUP, schema = FormFieldSchema.STRING, title = "Resource", description = "Google Ads resource to sync the data",
            group = GoogleAdsFormGroups.OBJECT_TYPE, optionsRef = @OptionsRef(value = OptionsReferences.OBJECT, type = OptionsRefType.DYNAMIC))
    private GAdsObjectType objectType;

    //customer match group
    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.STRING, title = "Customer Match Type", description = "Customer Match Type", group = GoogleAdsFormGroups.CUSTOMER_MATCH_TYPE,
            optionsRef = @OptionsRef(value = OptionsReferences.CUSTOMER_MATCH_TYPE, type = OptionsRefType.STATIC))
    private CustomerMatchType customerMatchType;

    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, title = "Customer Match List Name", description = "Customer Match list", group = GoogleAdsFormGroups.CUSTOMER_MATCH,
            optionsRef = @OptionsRef(value = OptionsReferences.GADS_SUB_RESOURCE, type = OptionsRefType.DYNAMIC))
    private GadsCustomerMatch customerMatch;

    //click conversions group
    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, title = "Click Conversion Name", group = GoogleAdsFormGroups.CLICK_CONVERSIONS,
            optionsRef = @OptionsRef(value = OptionsReferences.GADS_SUB_RESOURCE, type = OptionsRefType.DYNAMIC))
    private GadsConversion clickConversion;

    // call conversions group
    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.OBJECT, title = "Call Conversion Name", group = GoogleAdsFormGroups.CALL_CONVERSIONS,
            optionsRef = @OptionsRef(value = OptionsReferences.GADS_SUB_RESOURCE, type = OptionsRefType.DYNAMIC))
    private GadsConversion callConversion;

    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.STRING, title = "Conversion Time Zone", group = GoogleAdsFormGroups.ZONE_ID,
            optionsRef = @OptionsRef(value = OptionsReferences.ZONE_IDS, type = OptionsRefType.STATIC))
    private String zoneId;
}
