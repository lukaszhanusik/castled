package io.castled.commons.optionfetchers;

import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneIdOptionsFetcher implements StaticOptionsFetcher {
    @Override
    public List<FormFieldOption> getOptions() {
        return ZoneId.getAvailableZoneIds().stream().map(zoneId -> new FormFieldOption(zoneId, zoneId))
                .collect(Collectors.toList());
    }
}
