package io.castled.apps.connectors.googlesheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.ExternalAppConnector;
import io.castled.apps.models.ExternalAppSchema;
import io.castled.apps.models.MappingGroupAggregator;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.exceptions.connect.InvalidConfigException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.mapping.MappingGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class GoogleSheetsAppConnector implements ExternalAppConnector<GoogleSheetsAppConfig,
        GoogleSheetsDataWriter, GoogleSheetsAppSyncConfig> {

    @Override
    public List<FormFieldOption> getAllObjects(GoogleSheetsAppConfig config, GoogleSheetsAppSyncConfig mappingConfig) {
        try {
            Sheets sheetsService = GoogleSheetUtils.getSheets(config.getServiceAccount());
            Spreadsheet spreadsheet = sheetsService.spreadsheets()
                    .get(GoogleSheetUtils.getSpreadSheetId(config.getSpreadSheetId())).execute();
            return spreadsheet.getSheets().stream().map(Sheet::getProperties)
                    .map(sheetProperties -> new FormFieldOption(new GoogleSheetsSyncObject(sheetProperties.getSheetId(), sheetProperties.getTitle()),
                            sheetProperties.getTitle()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Gsheets get objects failed for {}", config.getServiceAccount().getClientEmail(), e);
            throw new CastledRuntimeException(e);
        }
    }

    public void validateAppConfig(GoogleSheetsAppConfig appConfig) throws InvalidConfigException {
        try {
            if (!GoogleSheetUtils.validSpreadSheetUrl(appConfig.getSpreadSheetId())) {
                throw new InvalidConfigException("SpreadSheet Url should be in the format: https://docs.google.com/spreadsheets/[/u/1]/d/spreadsheetId/edit.*");
            }
            Sheets sheets = GoogleSheetUtils.getSheets(appConfig.getServiceAccount());
            sheets.spreadsheets().get(GoogleSheetUtils.getSpreadSheetId(appConfig.getSpreadSheetId())).execute();
        } catch (Exception e) {
            if (e instanceof GoogleJsonResponseException) {
                GoogleJsonResponseException gre = (GoogleJsonResponseException) e;
                if (gre.getStatusCode() == 403) {
                    if (gre.getMessage().contains("Google Sheets API has not been used")) {
                        throw new InvalidConfigException("Google Sheets API needs to be enabled in Google Cloud Console");
                    }
                    throw new InvalidConfigException("Service account does not sufficient privileges to access the spreadsheet");
                }
            }
            throw new InvalidConfigException(e.getMessage());
        }
    }

    @Override
    public List<MappingGroup> getMappingGroups(GoogleSheetsAppConfig config, GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig) {
        return MappingGroupAggregator.builder().addElasticAppFields(null,"One or more needs to be marked as primary key",true, true).build().getMappingGroups();
    }

    @Override
    public GoogleSheetsDataWriter getDataSink() {
        return ObjectRegistry.getInstance(GoogleSheetsDataWriter.class);
    }

    @Override
    public ExternalAppSchema getSchema(GoogleSheetsAppConfig config, GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig) {
        return new ExternalAppSchema(null);
    }

    @Override
    public Class<GoogleSheetsAppSyncConfig> getMappingConfigType() {
        return GoogleSheetsAppSyncConfig.class;
    }

    @Override
    public Class<GoogleSheetsAppConfig> getAppConfigType() {
        return GoogleSheetsAppConfig.class;
    }
}
