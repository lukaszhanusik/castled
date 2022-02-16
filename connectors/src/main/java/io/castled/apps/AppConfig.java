package io.castled.apps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.castled.commons.serde.AppConfigDeserializer;
import lombok.Getter;

@JsonDeserialize(using = AppConfigDeserializer.class)
@Getter
public abstract class AppConfig {

    private ExternalAppType type;
}