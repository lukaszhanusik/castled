package io.castled.commons.models;

import com.google.common.collect.Lists;
import io.castled.schema.models.Field;
import io.castled.schema.models.Message;
import io.castled.schema.models.MessageOffsetSupplier;
import io.castled.schema.models.Tuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataSinkMessage implements MessageOffsetSupplier {

    public DataSinkMessage(Message message) {
        this(message, Lists.newArrayList());
    }

    private Message message;
    private List<Field> unmappedSourceFields;

    public long getOffset() {
        return message.getOffset();
    }

    public Tuple getRecord() {
        return message.getRecord();
    }
}
