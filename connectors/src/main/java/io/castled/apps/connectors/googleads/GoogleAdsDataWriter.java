package io.castled.apps.connectors.googleads;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.exceptions.CastledRuntimeException;

import java.util.Optional;

public class GoogleAdsDataWriter implements DataWriter {

    private GadsObjectSink gadsObjectSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        DataSinkMessage message;
        this.gadsObjectSink = getObjectSink((GoogleAdsAppSyncConfig) dataWriteRequest.getAppSyncConfig(),
                (GoogleAdsAppConfig) dataWriteRequest.getExternalApp().getConfig(),
                dataWriteRequest.getErrorOutputStream());
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            gadsObjectSink.writeRecord(message);
        }
        gadsObjectSink.flushRecords();

    }

    private GadsObjectSink getObjectSink(GoogleAdsAppSyncConfig mappingConfig,
                                         GoogleAdsAppConfig googleAdsAppConfig,
                                         ErrorOutputStream errorOutputStream) {

        switch (mappingConfig.getObjectType()) {
            case CUSTOMER_MATCH:
                return new CustomerMatchObjectSink(mappingConfig, googleAdsAppConfig, errorOutputStream);
            case CLICK_CONVERSIONS:
            case CALL_CONVERSIONS:
                return new ConversionObjectSink(mappingConfig, googleAdsAppConfig, errorOutputStream);
            default:
                throw new CastledRuntimeException(String.format("Unhandled sync object type %s", mappingConfig.getObjectType()));
        }
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(gadsObjectSink).map(GadsObjectSink::getSyncStats).
                map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0)).orElse(new AppSyncStats(0, 0, 0));
    }
}
