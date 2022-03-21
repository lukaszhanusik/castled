package io.castled.apps.connectors.Iterable.client;

import io.castled.exceptions.CastledException;

public class ApiLimitExceededException extends CastledException {

    public ApiLimitExceededException(String message) {
        super(message);
    }
}