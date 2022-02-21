package io.castled.apps.connectors.fbcustomaudience;

import com.google.inject.Singleton;
import io.castled.commons.errors.CastledError;
import io.castled.commons.errors.errorclassifications.InvalidHashError;
import io.castled.commons.errors.errorclassifications.UnclassifiedError;

@Singleton
public class FbErrorParser {

    private final String INVALID_HASH = "Invalid hash";

    public CastledError getPipelineError(String errorMsg) {
        if (errorMsg.contains(INVALID_HASH)) {
            return new InvalidHashError(errorMsg);
        }
        return new UnclassifiedError(errorMsg);
    }
}
