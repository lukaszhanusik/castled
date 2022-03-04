package io.castled.apps.connectors.marketo;

import io.castled.apps.BufferedObjectSink;
import io.castled.apps.DataSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.models.Message;

public class MarketoDataSink implements DataSink {

    private MarketoGenericObjectSink genericObjectSink = null;
    private MarketoLeadSink leadSink = null;

    @Override
    public void syncRecords(DataSinkRequest dataSinkRequest) throws Exception {

        BufferedObjectSink<DataSinkMessage> objectSink;
        MarketoObject marketoObject = MarketoObject
                .getObjectByName(((MarketoAppSyncConfig)dataSinkRequest.getAppSyncConfig()).getObject().getObjectName());
        switch (marketoObject) {
            case LEADS:
                this.leadSink = new MarketoLeadSink(dataSinkRequest);
                objectSink = leadSink;
                break;
            case COMPANIES:
            case OPPORTUNITIES:
                this.genericObjectSink = new MarketoGenericObjectSink(dataSinkRequest);
                objectSink = genericObjectSink;
                break;
            default:
                throw new CastledRuntimeException(String.format("Invalid object type %s!", marketoObject.getName()));
        }

        DataSinkMessage msg;
        while ((msg = dataSinkRequest.getMessageInputStream().readMessage()) != null) {
            objectSink.writeRecord(msg);
        }
        objectSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        if (leadSink != null) {
            return leadSink.getSyncStats();
        } else if (genericObjectSink != null) {
            return genericObjectSink.getSyncStats();
        } else {
            return new AppSyncStats(0, 0, 0);
        }
    }
}
