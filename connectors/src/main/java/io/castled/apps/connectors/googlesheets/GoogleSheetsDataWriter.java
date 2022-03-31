package io.castled.apps.connectors.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Lists;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.DataSinkMessage;
import io.castled.models.QueryMode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleSheetsDataWriter implements DataWriter {

    private GoogleSheetsObjectSink googleSheetsObjectSink;

    @Override
    public void writeRecords(DataWriteRequest dataWriteRequest) throws Exception {
        GoogleSheetsAppConfig googleSheetsAppConfig = (GoogleSheetsAppConfig) dataWriteRequest.getExternalApp().getConfig();
        GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig = (GoogleSheetsAppSyncConfig) dataWriteRequest.getAppSyncConfig();

        Sheets sheetsService = GoogleSheetUtils.getSheets(googleSheetsAppConfig.getServiceAccount());

        if (dataWriteRequest.getQueryMode() == QueryMode.FULL_LOAD) {
            sheetsService.spreadsheets().values().clear(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()), googleSheetsAppSyncConfig.getObject().getObjectName(),
                    new ClearValuesRequest()).execute();
        }

        List<SheetRow> sheetRows = dataWriteRequest.getQueryMode() == QueryMode.FULL_LOAD ? Lists.newArrayList() :
                GoogleSheetUtils.getRows(sheetsService, GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()),
                        googleSheetsAppSyncConfig.getObject().getObjectName());

        this.googleSheetsObjectSink = new GoogleSheetsObjectSink(googleSheetsAppConfig, googleSheetsAppSyncConfig, sheetsService,
                sheetRows, dataWriteRequest.getPrimaryKeys(), dataWriteRequest.getMappedFields(), dataWriteRequest.getErrorOutputStream());

        DataSinkMessage message;
        int recordsCount = 0;
        while ((message = dataWriteRequest.getMessageInputStream().readMessage()) != null) {
            if (recordsCount == 0 && CollectionUtils.isEmpty(sheetRows)) {
                sheetsService.spreadsheets().values().append(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()), googleSheetsAppSyncConfig.getObject().getObjectName(),
                                new ValueRange().setValues(Collections.singletonList(new ArrayList<>(dataWriteRequest.getMappedFields()))))
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
