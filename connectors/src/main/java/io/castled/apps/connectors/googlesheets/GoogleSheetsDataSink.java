package io.castled.apps.connectors.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;
import io.castled.apps.DataSink;
import io.castled.apps.models.DataSinkRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.models.QueryMode;
import io.castled.schema.models.Message;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleSheetsDataSink implements DataSink {

    private GoogleSheetsObjectSink googleSheetsObjectSink;

    @Override
    public void syncRecords(DataSinkRequest dataSinkRequest) throws Exception {
        GoogleSheetsAppConfig googleSheetsAppConfig = (GoogleSheetsAppConfig) dataSinkRequest.getExternalApp().getConfig();
        GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig = (GoogleSheetsAppSyncConfig) dataSinkRequest.getAppSyncConfig();

        Sheets sheetsService = GoogleSheetUtils.getSheets(googleSheetsAppConfig.getServiceAccount());

        if (dataSinkRequest.getQueryMode() == QueryMode.FULL_LOAD) {
            sheetsService.spreadsheets().values().clear(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()), googleSheetsAppSyncConfig.getObject().getObjectName(),
                    new ClearValuesRequest()).execute();
        }

        List<SheetRow> sheetRows = dataSinkRequest.getQueryMode() == QueryMode.FULL_LOAD ? Lists.newArrayList() :
                GoogleSheetUtils.getRows(sheetsService, GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()),
                        googleSheetsAppSyncConfig.getObject().getObjectName());

        this.googleSheetsObjectSink = new GoogleSheetsObjectSink(googleSheetsAppConfig, googleSheetsAppSyncConfig, sheetsService,
                sheetRows, dataSinkRequest.getPrimaryKeys(), dataSinkRequest.getMappedFields(), dataSinkRequest.getErrorOutputStream());

        DataSinkMessage message;
        int recordsCount = 0;
        while ((message = dataSinkRequest.getMessageInputStream().readMessage()) != null) {
            if (recordsCount == 0 && CollectionUtils.isEmpty(sheetRows)) {
                sheetsService.spreadsheets().values().append(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()), googleSheetsAppSyncConfig.getObject().getObjectName(),
                                new ValueRange().setValues(Collections.singletonList(new ArrayList<>(dataSinkRequest.getMappedFields()))))
                        .setValueInputOption("USER_ENTERED").execute();
            }
            this.googleSheetsObjectSink.writeRecord(message);
            recordsCount++;
        }
        this.googleSheetsObjectSink.flushRecords();
    }

    @Override
    public AppSyncStats getSyncStats() {
        return Optional.ofNullable(this.googleSheetsObjectSink)
                .map(audienceSinkRef -> this.googleSheetsObjectSink.getSyncStats())
                .map(statsRef -> new AppSyncStats(statsRef.getRecordsProcessed(), statsRef.getOffset(), 0))
                .orElse(new AppSyncStats(0, 0, 0));
    }
}
