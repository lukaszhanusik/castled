package io.castled.apps.connectors.hubspot.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HubspotPropertyResponse {

    private List<HubspotProperty> properties;
}
