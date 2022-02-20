package io.castled.apps.syncconfigs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.castled.apps.ExternalAppType;
import io.castled.commons.serde.AppSyncConfigDeserializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize(using = AppSyncConfigDeserializer.class)
public abstract class AppSyncConfig {

    private ExternalAppType appType;
}
