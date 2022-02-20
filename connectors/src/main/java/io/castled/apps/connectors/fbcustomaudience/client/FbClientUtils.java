package io.castled.apps.connectors.fbcustomaudience.client;

import io.castled.apps.connectors.fbcustomaudience.client.dtos.FbErrorResponse;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.utils.ResponseUtils;

import javax.ws.rs.core.Response;
import java.util.function.Supplier;

public class FbClientUtils {

    public static Response executeAndHandleError(Supplier<Response> supplier) {
        Response response = supplier.get();
        if (!ResponseUtils.is2xx(response)) {
            FbErrorResponse error = response.readEntity(FbErrorResponse.class);
            throw new CastledRuntimeException(String.format("%s:%s", error.getError().getType(), error.getError().getMessage()));
        }
        return response;
    }
}
