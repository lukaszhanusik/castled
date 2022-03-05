package io.castled.commons.streams;

import io.castled.commons.models.DataSinkMessage;

public interface DataSinkMessageInputStream {

    DataSinkMessage readMessage() throws Exception;

}
