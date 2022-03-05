package io.castled.commons.streams;

import io.castled.commons.models.DataSinkMessage;

public interface DataSinkMessageOutputStream {

    void writeDataSinkMessage(DataSinkMessage dataSinkMessage) throws Exception;

    void flush() throws Exception;
}
