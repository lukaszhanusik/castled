package io.castled.apps.connectors.Iterable;

import com.google.inject.Singleton;
import io.castled.apps.connectors.Iterable.client.dtos.UserPrimaryKey;
import io.castled.commons.errors.CastledError;
import io.castled.commons.errors.errorclassifications.InvalidFieldValueError;
import io.castled.commons.errors.errorclassifications.UnclassifiedError;

@Singleton
public class IterableErrorParser {

    public static final String ERR_INVALID_EMAIL = "Invalid email";
    public static final String ERR_INVALID_USER_ID = "Invalid userId";

    public CastledError getPipelineError(String fieldName) {
        if (fieldName.contains(UserPrimaryKey.EMAIL.getName())) {
            return new InvalidFieldValueError(fieldName, fieldName, ERR_INVALID_EMAIL);
        } else if (fieldName.contains(UserPrimaryKey.EMAIL.getName())) {
            return new InvalidFieldValueError(fieldName, fieldName, ERR_INVALID_USER_ID);
        }
        return new UnclassifiedError("Unknown error!");
    }
}