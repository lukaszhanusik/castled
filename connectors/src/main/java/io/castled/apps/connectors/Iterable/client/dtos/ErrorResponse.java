package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {

    private String msg;
    private String code;
    private Map<String, Object> params;
}
