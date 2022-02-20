package io.castled.dtos;

import io.castled.apps.AppConfig;
import io.castled.apps.OAuthAppConfig;
import lombok.Data;

@Data
public class OAuthAppAttributes {
    private String name;
    private AppConfig config;
    private String successUrl;
    private String failureUrl;
    private String serverUrl;
}
