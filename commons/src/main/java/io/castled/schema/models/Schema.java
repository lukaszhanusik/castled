package io.castled.schema.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.castled.schema.exceptions.IncompatibleValueException;
import io.castled.schema.exceptions.NullValueException;
import io.castled.schema.exceptions.SchemaValidationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@NoArgsConstructor
public abstract class Schema {

    private SchemaType type;
    private List<Class<?>> allowedTypes;
    private boolean optional;

    public void validateValue(String fieldName, Object value) throws SchemaValidationException {

        if (value == null) {
            if (!optional) {
                throw new NullValueException(fieldName, type);
            }
            return;
        }
        if (allowedTypes.stream().noneMatch(allowedType -> allowedType.isInstance(value))) {
            throw new IncompatibleValueException(type, value);
        }
    }
}
