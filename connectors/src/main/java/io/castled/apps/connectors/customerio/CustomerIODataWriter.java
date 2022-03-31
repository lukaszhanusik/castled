package io.castled.apps.connectors.customerio;

import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;

import java.util.List;
import java.util.Optional;

public class CustomerIODataWriter implements DataWriter {


    private volatile CustomerIOObjectSink<String> customerIOObjectSink;

    private long skippedRecords = 0;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {

        this.customerIOObjectSink = getObjectSink(dataWriteRequest);
        DataSinkMessage message;
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            if (!this.writeRecord(message, dataWriteRequest.getPrimaryKeys())) {
                skippedRecords++;
            }
        }
        this.customerIOObjectSink.flushRecords();
    }

    private CustomerIOObjectSink<String> getObjectSink(DataWriteRequest dataWriteRequest) {
        CustomerIOObjectSink<String> customerIOObjectSink = null;
        CustomerIOObject customerIOObject = CustomerIOObject
                .getObjectByName(((CustomerIOAppSyncConfig) dataWriteRequest.getAppSyncConfig()).getObject().getObjectName());
        switch (customerIOObject) {
            case EVENT:
                customerIOObjectSink = new CustomerIOEventSink(dataWriteRequest);
                break;
            case PERSON:
                customerIOObjectSink = new CustomerIOPersonSink(dataWriteRequest);
                break;
            default:
                throw new CastledRuntimeException(String.format("Invalid object type %s!", customerIOObject.getName()));
        }
        return customerIOObjectSink;
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(customerIOObjectSink).map(CustomerIOObjectSink::getSyncStats).
                map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(),
                        statsRef.getOffset(), skippedRecords)).orElse(new AppSyncStats(0, 0, 0));
    }

    private boolean writeRecord(DataSinkMessage message,List<String> primaryKeys) {
        this.customerIOObjectSink.createOrUpdateObject(message);
        return true;
    }
}
