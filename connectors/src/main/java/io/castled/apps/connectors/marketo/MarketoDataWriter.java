package io.castled.apps.connectors.marketo;

import io.castled.apps.BufferedObjectSink;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;

public class MarketoDataWriter implements DataWriter {

    private MarketoGenericObjectSink genericObjectSink = null;
    private MarketoLeadSink leadSink = null;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        BufferedObjectSink<DataSinkMessage> objectSink;
        MarketoObject marketoObject = MarketoObject
                .getObjectByName(((MarketoAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject().getObjectName());
        switch (marketoObject) {
            case LEADS:
                this.leadSink = new MarketoLeadSink(dataWriteRequest);
                objectSink = leadSink;
                break;
            case COMPANIES:
            case OPPORTUNITIES:
                this.genericObjectSink = new MarketoGenericObjectSink(dataWriteRequest);
                objectSink = genericObjectSink;
                break;
            default:
                throw new CastledRuntimeException(String.format("Invalid object type %s!", marketoObject.getName()));
        }

        DataSinkMessage msg;
        while ((msg = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
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
