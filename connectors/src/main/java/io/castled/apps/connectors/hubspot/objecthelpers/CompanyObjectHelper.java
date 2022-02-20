package io.castled.apps.connectors.hubspot.objecthelpers;

import com.google.common.collect.Lists;
import io.castled.apps.connectors.hubspot.HubspotFields;
import io.castled.apps.connectors.hubspot.client.dtos.HubspotProperty;

import java.util.List;

public class CompanyObjectHelper implements HubspotObjectHelper {

    public List<String> dedupKeyEligibles(List<HubspotProperty> allProperties) {
        return Lists.newArrayList(HubspotFields.COMPANY_DOMAIN);
    }
}
