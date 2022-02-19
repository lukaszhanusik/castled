package io.castled.apps.connectors.fbcustomaudience;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;
import java.util.stream.Collectors;

public class FbCustomerErrors {

    Integer numInvalidEntries;
    Map<String, String> invalidEntrySamples;

    public FbCustomerErrors(Integer numInvalidEntries, Map<String, String> invalidEntrySamples) {
        this.numInvalidEntries = numInvalidEntries;
        this.invalidEntrySamples = invalidEntrySamples.keySet().stream()
                .collect(Collectors.toMap(StringEscapeUtils::unescapeJava, invalidEntrySamples::get));
    }
}
