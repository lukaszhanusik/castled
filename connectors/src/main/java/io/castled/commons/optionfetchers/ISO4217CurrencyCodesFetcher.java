package io.castled.commons.optionfetchers;

import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

public class ISO4217CurrencyCodesFetcher implements StaticOptionsFetcher {
    @Override
    public List<FormFieldOption> getOptions() {
        return Currency.getAvailableCurrencies().stream()
                .map(currency -> new FormFieldOption(currency.getCurrencyCode(), currency.getDisplayName()))
                .collect(Collectors.toList());
    }
}
