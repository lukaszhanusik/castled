package io.castled.apps.connectors.fbconversion;

import com.google.inject.Singleton;
import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class FbActionSourceOptionsFetcher implements StaticOptionsFetcher {

    private enum ActionSources {
        EMAIL("email", "Conversion happened over email"),
        WEBSITE("website", "Conversion was made on your website"),
        PHONE_CALL("phone_call", "Conversion was made over the phone"),
        CHAT("chat", "Conversion was made via a messaging app, SMS, or online messaging feature"),
        PHYSICAL_STORE("physical_store", "Conversion was made in person at your physical store"),
        SYSTEM_GENERATED("system_generated", "Conversion happened automatically, for example, a subscription renewal that’s set to auto-pay each month"),
        OTHER("other", "Conversion happened in a way that is not listed");

        @Getter
        private String name;

        @Getter
        private String description;

        ActionSources(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    @Override
    public List<FormFieldOption> getOptions() {
        List<FormFieldOption> formFieldOptions = Arrays.stream(ActionSources.values())
                .map(enumVal -> new FormFieldOption(enumVal.getName(), enumVal.getName(), enumVal.getDescription()))
                .collect(Collectors.toList());
        return formFieldOptions;
    }
}

