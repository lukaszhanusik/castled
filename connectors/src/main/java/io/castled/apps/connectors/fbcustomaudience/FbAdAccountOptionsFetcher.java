package io.castled.apps.connectors.fbcustomaudience;

import com.google.api.client.util.Lists;
import com.google.inject.Singleton;
import io.castled.apps.ExternalApp;
import io.castled.apps.connectors.fbcustomaudience.client.FbRestClient;
import io.castled.apps.connectors.fbcustomaudience.client.dtos.AdAccount;
import io.castled.apps.dtos.AppSyncConfigDTO;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.optionsfetchers.appsync.AppSyncOptionsFetcher;

import java.util.List;

@Singleton
public class FbAdAccountOptionsFetcher implements AppSyncOptionsFetcher {

    @Override
    public List<FormFieldOption> getOptions(AppSyncConfigDTO appSyncConfig, ExternalApp externalApp) {
        FbRestClient client = new FbRestClient((FbAppConfig) externalApp.getConfig(),
                (FbCustomAudAppSyncConfig) appSyncConfig.getAppSyncConfig());
        List<AdAccount> adAccounts = client.getAllAdAccounts();
        List<FormFieldOption> formFieldOptions = Lists.newArrayList();
        adAccounts.forEach(adAccount ->
                formFieldOptions.add(new FormFieldOption(adAccount.getId(), adAccount.getName())));
        return formFieldOptions;
    }
}