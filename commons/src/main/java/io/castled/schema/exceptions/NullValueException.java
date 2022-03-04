package io.castled.schema.exceptions;

import io.castled.schema.models.SchemaType;

public class NullValueException extends SchemaValidationException {

    public NullValueException(String fieldName, SchemaType schemaType) {
        super(schemaType, String.format("Value absent for required field %s", fieldName));
    }
}
