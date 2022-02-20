package io.castled.apps.connectors.hubspot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HubspotSyncObject {

    private String name;
    private String typeId;
}
