package io.castled.apps.connectors.Iterable;

import io.castled.apps.ExternalApp;
import io.castled.apps.connectors.Iterable.client.IterableRestClient;
import io.castled.apps.connectors.Iterable.client.dtos.TemplateId;
import io.castled.apps.dtos.AppSyncConfigDTO;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.optionsfetchers.appsync.AppSyncOptionsFetcher;

import java.util.List;
import java.util.stream.Collectors;

public class IterableTemplateOptionsFetcher implements AppSyncOptionsFetcher {

    @Override
    public List<FormFieldOption> getOptions(AppSyncConfigDTO config, ExternalApp externalApp) {
        IterableRestClient iterableRestClient = new IterableRestClient((IterableAppConfig) externalApp.getConfig());
        List<TemplateId> templateIds = iterableRestClient.getTemplates();
        return templateIds.stream().map(templateId -> new FormFieldOption(templateId.getTemplateId().toString(), templateId.getName()))
                .collect(Collectors.toList());
    }
}