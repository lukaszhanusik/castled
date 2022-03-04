package io.castled.commons.streams;

import com.google.inject.Singleton;
import io.castled.commons.models.DataSinkMessage;

@Singleton
public class DefaultDataSinkMessageOutputStream implements DataSinkMessageOutputStream {

    private final RecordOutputStream recordOutputStream;

    public DefaultDataSinkMessageOutputStream(RecordOutputStream recordOutputStream) {
        this.recordOutputStream = recordOutputStream;
    }

    @Override
    public void writeDataSinkMessage(DataSinkMessage dataSinkMessage) throws Exception {
        this.recordOutputStream.writeRecord(dataSinkMessage.getRecord());
    }

    @Override
    public void flush() throws Exception {
        this.recordOutputStream.flush();
    }
}
