package io.castled.pipelines;

import com.google.common.collect.Lists;
import io.castled.ObjectRegistry;
import io.castled.commons.errors.errorclassifications.IncompatibleMappingError;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.streams.DataSinkMessageInputStream;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.commons.streams.MessageInputStream;
import io.castled.schema.IncompatibleValueException;
import io.castled.schema.SchemaMapper;
import io.castled.schema.models.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SchemaMappedMessageInputStream implements DataSinkMessageInputStream {

    private final RecordSchema targetSchema;
    private final MessageInputStream messageInputStream;
    private final SchemaMapper schemaMapper;
    private final Map<String, List<String>> targetSourceMapping;
    private final Map<String, List<String>> sourceTargetMapping;
    private final ErrorOutputStream errorOutputStream;
    @Getter
    private long failedRecords = 0;

    public SchemaMappedMessageInputStream(RecordSchema targetSchema, MessageInputStream messageInputStream,
                                          Map<String, List<String>> targetSourceMapping, Map<String, List<String>> sourceTargetMapping, ErrorOutputStream errorOutputStream) {
        this.targetSchema = targetSchema;
        this.messageInputStream = messageInputStream;
        this.schemaMapper = ObjectRegistry.getInstance(SchemaMapper.class);
        this.targetSourceMapping = targetSourceMapping;
        this.errorOutputStream = errorOutputStream;
        this.sourceTargetMapping = sourceTargetMapping;
    }

    @Override
    public DataSinkMessage readMessage() throws Exception {

        while (true) {
            Message message = this.messageInputStream.readMessage();
            if (message == null) {
                return null;
            }
            if (this.sourceTargetMapping == null) {
                return new DataSinkMessage(message);
            }
            DataSinkMessage mappedMessage = mapMessage(message);
            if (mappedMessage == null) {
                continue;
            }
            return mappedMessage;
        }
    }

    private DataSinkMessage mapMessage(Message message) {

        if (targetSchema == null) {
            return new DataSinkMessage(mapMessageFromSourceSchema(message));
        }
        Tuple.Builder recordBuilder = Tuple.builder();
        List<String> mappedSourceFields = Lists.newArrayList();
        for (FieldSchema field : targetSchema.getFieldSchemas()) {
            List<String> sourceFields = targetSourceMapping.get(field.getName());
            if (!CollectionUtils.isEmpty(sourceFields)) {
                for (String sourceField : sourceFields) {
                    if (sourceField != null) {
                        try {
                            recordBuilder.put(field, schemaMapper.transformValue(message.getRecord().getValue(sourceField), field.getSchema()));
                            mappedSourceFields.add(sourceField);
                        } catch (IncompatibleValueException e) {
                            failedRecords++;
                            this.errorOutputStream.writeFailedRecord(new DataSinkMessage(message), new IncompatibleMappingError(sourceField, field.getSchema()));
                            return null;
                        }
                    }
                }
            }
        }
        List<Field> unmappedSourceFields = message.getRecord().getFields().stream()
                .filter(field -> !mappedSourceFields.contains(field.getName())).collect(Collectors.toList());
        return new DataSinkMessage(new Message(message.getOffset(), recordBuilder.build()), unmappedSourceFields);
    }

    private Message mapMessageFromSourceSchema(Message message) {
        Tuple.Builder recordBuilder = Tuple.builder();
        for (Field field : message.getRecord().getFields()) {
            List<String> targetFields = sourceTargetMapping.get(field.getName());
            if (!CollectionUtils.isEmpty(targetFields)) {
                targetFields.forEach(targetField -> recordBuilder.put(new FieldSchema(targetField, field.getSchema(), field.getParams()), field.getValue()));
            }
        }
        return new Message(message.getOffset(), recordBuilder.build());
    }

    public void close() throws Exception {
        this.messageInputStream.close();
    }
}
