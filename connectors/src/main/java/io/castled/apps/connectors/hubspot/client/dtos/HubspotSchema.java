package io.castled.apps.connectors.hubspot.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HubspotSchema {
    private String name;
    private String objectTypeId;
}
