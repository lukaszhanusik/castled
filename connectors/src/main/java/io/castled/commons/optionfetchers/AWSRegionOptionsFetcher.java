package io.castled.commons.optionfetchers;

import com.amazonaws.regions.Regions;
import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AWSRegionOptionsFetcher implements StaticOptionsFetcher {
    @Override
    public List<FormFieldOption> getOptions() {
        return Arrays.stream(Regions.values()).map(region -> new FormFieldOption(region, region.getDescription()))
                .collect(Collectors.toList());
    }
}
