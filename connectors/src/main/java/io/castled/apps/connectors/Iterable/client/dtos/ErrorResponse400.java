package io.castled.apps.connectors.Iterable.client.dtos;

import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse400 {

    private String error;
    private String message;
    Map<String, Object> data;
}
