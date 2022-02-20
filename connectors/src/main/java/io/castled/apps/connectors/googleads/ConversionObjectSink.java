package io.castled.apps.connectors.googleads;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v7.resources.ConversionCustomVariable;
import com.google.ads.googleads.v7.services.*;
import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.commons.errors.errorclassifications.MissingRequiredFieldsError;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.oauth.OAuthDetails;
import io.castled.schema.models.Field;
import io.castled.schema.models.Message;
import io.castled.schema.models.Tuple;
import io.castled.services.OAuthService;
import io.castled.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Slf4j
public class ConversionObjectSink extends GadsObjectSink {

    private final GoogleAdsAppSyncConfig mappingConfig;
    private final ConversionUploadServiceClient conversionUploadServiceClient;
    private final List<ConversionCustomVariable> customVariables;

    public ConversionObjectSink(GoogleAdsAppSyncConfig mappingConfig,
                                GoogleAdsAppConfig googleAdsAppConfig, ErrorOutputStream errorOutputStream) {
        super(errorOutputStream);
        this.mappingConfig = mappingConfig;

        OAuthDetails oAuthDetails = ObjectRegistry.getInstance(OAuthService.class).getOAuthDetails(googleAdsAppConfig.getOAuthToken());
        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
                .fromProperties(GoogleAdUtils.getClientProperties(googleAdsAppConfig, oAuthDetails.getAccessConfig().getRefreshToken(), mappingConfig.getLoginCustomerId())).build();
        this.conversionUploadServiceClient = googleAdsClient.getLatestVersion().createConversionUploadServiceClient();
        this.customVariables = GoogleAdUtils.getCustomVariables(googleAdsAppConfig, mappingConfig);
    }

    public void writeRecords(List<Message> messages) {
        GAdsObjectType gAdsObjectType = mappingConfig.getObjectType();
        if (gAdsObjectType == GAdsObjectType.CLICK_CONVERSIONS) {
            uploadClickConversions(messages);
        } else {
            uploadCallConversions(messages);
        }
        this.processedRecords.addAndGet(messages.size());
        this.lastProcessedMessageId = Math.min(lastProcessedMessageId, messages.get(messages.size() - 1).getOffset());
    }

    private void uploadClickConversions(List<Message> messages) {

        List<ClickConversion> clickConversions = Lists.newArrayList();
        for (Message message : messages) {
            ClickConversion clickConversion = getClickConversion(message);
            if (!clickConversion.hasConversionDateTime()) {
                errorOutputStream.writeFailedRecord(message,
                        new MissingRequiredFieldsError(Lists.newArrayList(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName())));
                continue;
            }
            if (!clickConversion.hasGclid()) {
                errorOutputStream.writeFailedRecord(message,
                        new MissingRequiredFieldsError(Lists.newArrayList(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.GCLID.getFieldName())));
                continue;
            }
            clickConversions.add(clickConversion);
        }
        UploadClickConversionsResponse response =
                conversionUploadServiceClient.uploadClickConversions(
                        UploadClickConversionsRequest.newBuilder().setCustomerId(String.valueOf(mappingConfig.getAccountId()))
                                .addAllConversions(clickConversions).setPartialFailure(true).build());

        if (response.hasPartialFailureError()) {
            handlePartialFailures(messages, response.getPartialFailureError());
        }
    }

    private ClickConversion getClickConversion(Message message) {

        Tuple record = message.getRecord();
        Double conversionValue = (Double) record.getValue(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CONVERSION_VALUE.getFieldName());
        String orderId = (String) record.getValue(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.ORDER_ID.getFieldName());
        String gclId = (String) record.getValue(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.GCLID.getFieldName());
        String currencyCode = (String) record.getValue(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CURRENCY_CODE.getFieldName());

        LocalDateTime conversionTime = (LocalDateTime) record.getValue(GadsObjectFields.CLICK_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName());
        String conversionDateTimeString = Optional.ofNullable(conversionTime).map(this::formatTimestampField).orElse(null);

        ClickConversion.Builder builder = ClickConversion.newBuilder()
                .setConversionAction(mappingConfig.getClickConversion().getResourceName());

        Optional.ofNullable(gclId).ifPresent(builder::setGclid);
        Optional.ofNullable(conversionValue).ifPresent(builder::setConversionValue);
        Optional.ofNullable(orderId).ifPresent(builder::setOrderId);
        Optional.ofNullable(currencyCode).ifPresent(builder::setCurrencyCode);
        Optional.ofNullable(conversionDateTimeString).ifPresent(builder::setConversionDateTime);

        for (ConversionCustomVariable customVariable : customVariables) {
            String customVariableValue = MessageUtils.toString(record.getField(customVariable.getName()));
            Optional.ofNullable(customVariableValue).ifPresent(valueRef -> builder.addCustomVariables(
                    CustomVariable.newBuilder()
                            .setConversionCustomVariable(customVariable.getResourceName())
                            .setValue(valueRef)));
        }

        return builder.build();

    }

    private String formatTimestampField(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(mappingConfig.getZoneId()));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx"));
    }


    private CallConversion getCallConversion(Message message) {
        Tuple record = message.getRecord();

        Double conversionValue = (Double) record.getValue(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CONVERSION_VALUE.getFieldName());
        String callerId = (String) record.getValue(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALLER_ID.getFieldName());
        String currencyCode = (String) record.getValue(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CURRENCY_CODE.getFieldName());

        LocalDateTime conversionDateTime = (LocalDateTime) record.getValue(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName());
        String conversionDateTimeString = Optional.ofNullable(conversionDateTime).map(this::formatTimestampField).orElse(null);

        LocalDateTime callStartTime = (LocalDateTime) record.getValue(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALL_START_TIME.getFieldName());
        String callStartTimeString = Optional.ofNullable(callStartTime).map(this::formatTimestampField).orElse(null);

        CallConversion.Builder builder = CallConversion.newBuilder()
                .setConversionAction(mappingConfig.getCallConversion().getResourceName());

        Optional.ofNullable(callerId).ifPresent(builder::setCallerId);
        Optional.ofNullable(conversionValue).ifPresent(builder::setConversionValue);
        Optional.ofNullable(currencyCode).ifPresent(builder::setCurrencyCode);
        Optional.ofNullable(conversionDateTimeString).ifPresent(builder::setConversionDateTime);

        Optional.ofNullable(callStartTimeString).ifPresent(builder::setCallStartDateTime);

        for (ConversionCustomVariable customVariable : customVariables) {
            String customVariableValue = (String) record.getValue(customVariable.getName());
            Optional.ofNullable(customVariableValue).ifPresent(valueRef -> builder.addCustomVariables(
                    CustomVariable.newBuilder()
                            .setConversionCustomVariable(customVariable.getResourceName())
                            .setValue(valueRef)));
        }
        return builder.build();
    }

    private void uploadCallConversions(List<Message> messages) {

        List<CallConversion> callConversions = Lists.newArrayList();
        for (Message message : messages) {
            CallConversion callConversion = getCallConversion(message);
            if (!callConversion.hasConversionDateTime()) {
                errorOutputStream.writeFailedRecord(message,
                        new MissingRequiredFieldsError(Lists.newArrayList(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CONVERSION_TIME.getFieldName())));
                continue;
            }
            if (!callConversion.hasCallerId()) {
                errorOutputStream.writeFailedRecord(message,
                        new MissingRequiredFieldsError(Lists.newArrayList(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALLER_ID.getFieldName())));
                continue;
            }
            if (!callConversion.hasCallStartDateTime()) {
                errorOutputStream.writeFailedRecord(message,
                        new MissingRequiredFieldsError(Lists.newArrayList(GadsObjectFields.CALL_CONVERSION_STANDARD_FIELDS.CALL_START_TIME.getFieldName())));
                continue;
            }
            callConversions.add(callConversion);
        }

        UploadCallConversionsResponse response =
                conversionUploadServiceClient.uploadCallConversions(
                        UploadCallConversionsRequest.newBuilder().setCustomerId(mappingConfig.getAccountId())
                                .addAllConversions(callConversions).setPartialFailure(true).build());
        if (response.hasPartialFailureError()) {
            handlePartialFailures(messages, response.getPartialFailureError());
        }
    }
}
