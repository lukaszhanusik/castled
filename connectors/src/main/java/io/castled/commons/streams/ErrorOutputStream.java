package io.castled.commons.streams;

import io.castled.commons.errors.CastledError;
import io.castled.commons.errors.CastledErrorTracker;
import io.castled.commons.models.DataSinkMessage;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.schema.models.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ErrorOutputStream {

    private final DataSinkMessageOutputStream dataSinkMessageOutputStream;
    private final CastledErrorTracker castledErrorTracker;

    @Getter
    private final AtomicLong failedRecords = new AtomicLong(0);
    @Getter
    private volatile Long firstFailedMessageId;

    public ErrorOutputStream(DataSinkMessageOutputStream dataSinkMessageOutputStream, CastledErrorTracker castledErrorTracker) {
        this.dataSinkMessageOutputStream = dataSinkMessageOutputStream;
        this.castledErrorTracker = castledErrorTracker;
    }

    public void writeFailedRecord(DataSinkMessage dataSinkMessage, CastledError pipelineError) {
        try {
            if (firstFailedMessageId == null) {
                firstFailedMessageId = dataSinkMessage.getOffset();
            }
            this.failedRecords.incrementAndGet();
            this.castledErrorTracker.writeError(dataSinkMessage.getRecord(), pipelineError);
            this.dataSinkMessageOutputStream.writeDataSinkMessage(dataSinkMessage);
        } catch (Exception e) {
            log.error(String.format("Write failed record failed for error %s", pipelineError.description()), e);
            throw new CastledRuntimeException(e);
        }
    }

    public void flushFailedRecords() throws Exception {
        this.dataSinkMessageOutputStream.flush();
        this.castledErrorTracker.flushErrors();
    }

}
