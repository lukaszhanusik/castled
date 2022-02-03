package io.castled.apps.connectors.restapi;

import io.castled.apps.BufferedObjectSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.errors.errorclassifications.UnclassifiedError;
import io.castled.commons.models.MessageSyncStats;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.models.TargetTemplateMapping;
import io.castled.schema.models.Message;

import java.util.List;

public class RestApiObjectSyncV2 extends BufferedObjectSink<Message> {

    private final TargetTemplateMapping targetTemplateMapping;
    private final ErrorOutputStream errorOutputStream;

    public RestApiObjectSyncV2(DataSinkRequest dataSinkRequest) {
        this.targetTemplateMapping = (TargetTemplateMapping) dataSinkRequest.getMapping();
        this.errorOutputStream = dataSinkRequest.getErrorOutputStream();
    }

    @Override
    protected void writeRecords(List<Message> messages) {
        for (Message message : messages) {
            this.errorOutputStream.writeFailedRecord(message, new UnclassifiedError("abcd"));
        }
    }

    @Override
    public long getMaxBufferedObjects() {
        return 0;
    }

    public MessageSyncStats getSyncStats() {
        return new MessageSyncStats(0, 0);
    }
}
