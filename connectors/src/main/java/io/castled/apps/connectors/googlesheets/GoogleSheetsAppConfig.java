package io.castled.apps.connectors.googlesheets;

import io.castled.apps.AppConfig;
import io.castled.commons.models.ServiceAccountDetails;
import io.castled.forms.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@HelpText(value = "Provide Editor access of your google sheets to this email:  \n" +
        "```${serviceAccount.client_email}```", dependencies = {"spreadSheetId", "serviceAccount"})
@GroupActivator(dependencies = {"spreadSheetId"}, group = "service_account")
public class GoogleSheetsAppConfig extends AppConfig {

    @FormField(type = FormFieldType.TEXT_BOX, title = "Spread Sheet Url", placeholder = "e.g. https://docs.google.com/spreadsheets/[/u/1]/d/spreadsheetId/edit.*", description = "Spread Sheet Id", schema = FormFieldSchema.STRING)
    private String spreadSheetId;

    @FormField(type = FormFieldType.JSON_FILE, schema = FormFieldSchema.OBJECT, description = "Service Account Json File", title = "Service Account Json File", group = "service_account")
    private ServiceAccountDetails serviceAccount;
}
