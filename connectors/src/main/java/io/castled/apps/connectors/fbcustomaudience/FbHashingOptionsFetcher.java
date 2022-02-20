package io.castled.apps.connectors.fbcustomaudience;


import com.google.api.client.util.Lists;
import com.google.inject.Singleton;
import io.castled.forms.StaticOptionsFetcher;
import io.castled.forms.dtos.FormFieldOption;

import java.util.List;

@Singleton
public class FbHashingOptionsFetcher  implements StaticOptionsFetcher  {
    @Override
    public List<FormFieldOption> getOptions() {
        List<FormFieldOption> formFieldOptions = Lists.newArrayList();
        formFieldOptions.add(new FormFieldOption(true, "Yes", "Let Castled hash the Personally Identifiable Information (PII) for you."));
        formFieldOptions.add(new FormFieldOption(false, "No", "I will hash the Personally Identifiable Information (PII) myself."));
        return formFieldOptions;
    }
}
