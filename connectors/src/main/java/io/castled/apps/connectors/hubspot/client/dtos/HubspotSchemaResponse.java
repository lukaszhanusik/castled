package io.castled.apps.connectors.hubspot.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HubspotSchemaResponse {
    private List<HubspotSchema> results;
}
