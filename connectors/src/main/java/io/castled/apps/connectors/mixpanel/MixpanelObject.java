package io.castled.apps.connectors.mixpanel;

import io.castled.exceptions.CastledRuntimeException;
import lombok.Getter;

import java.util.Arrays;

public enum MixpanelObject {

    EVENT("Event", "Sync event batches from your warehouse to Mixpanel."),
    GROUP_PROFILE("Group Profile", "Update properties to a group profile. The profile is created if it does not exist."),
    USER_PROFILE("User Profile", "Update properties to a group profile. The profile is created if it does not exist.");

    @Getter
    private final String name;

    @Getter
    private final String description;

    MixpanelObject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static MixpanelObject getObjectByName(String name) {
        return Arrays.stream(MixpanelObject.values()).filter(intercomObject -> intercomObject.getName().equals(name))
                .findFirst().orElseThrow(() -> new CastledRuntimeException(String.format("Invalid object name %s", name)));
    }

}
