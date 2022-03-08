package io.castled.apps.connectors.fbconversion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FbConversionStatus {

    private long eventsReceived;
    private String errorUserTitle;
    private String errorUserMsg;
}
