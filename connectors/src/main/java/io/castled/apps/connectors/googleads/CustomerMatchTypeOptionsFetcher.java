package io.castled.apps.connectors.googleads;

import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerMatchTypeOptionsFetcher implements StaticOptionsFetcher {
    @Override
    public List<FormFieldOption> getOptions() {
        return Arrays.stream(CustomerMatchType.values())
                .map(customerMatchType -> new FormFieldOption(customerMatchType, customerMatchType.name()))
                .collect(Collectors.toList());
    }
}
