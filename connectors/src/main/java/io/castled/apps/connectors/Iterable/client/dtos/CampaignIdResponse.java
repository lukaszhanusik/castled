package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
public class CampaignIdResponse {

    private List<CampaignId> campaigns;
}
