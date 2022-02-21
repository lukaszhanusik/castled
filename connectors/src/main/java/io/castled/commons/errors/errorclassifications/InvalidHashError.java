package io.castled.commons.errors.errorclassifications;

import io.castled.commons.errors.CastledError;
import io.castled.commons.errors.CastledErrorCode;
import lombok.Getter;

@Getter
public class InvalidHashError extends CastledError {
    private final String errorMsg;
    private final String INVALID_HASH = "Invalid hash";

    public InvalidHashError(String errorMsg) {
        super(CastledErrorCode.INVALID_HASH);
        this.errorMsg = errorMsg;
    }

    @Override
    public String uniqueId() {
        return INVALID_HASH;
    }

    @Override
    public String description() {
        return errorMsg;
    }
}
