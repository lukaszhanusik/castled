package io.castled.apps.connectors.googlesheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.castled.apps.BufferedObjectSink;
import io.castled.commons.errors.errorclassifications.ExternallyCategorizedError;
import io.castled.commons.errors.errorclassifications.UnclassifiedError;
import io.castled.commons.models.DataSinkMessage;
import io.castled.commons.models.MessageSyncStats;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.forms.dtos.FormFieldOption;
import io.castled.schema.models.Field;
import io.castled.schema.models.Message;
import io.castled.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.snowflake.client.jdbc.internal.apache.arrow.flatbuf.Int;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class GoogleSheetsObjectSink extends BufferedObjectSink<DataSinkMessage> {

    private final GoogleSheetsAppConfig googleSheetsAppConfig;
    private final GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig;
    private final List<String> primaryKeys;
    private final AtomicLong processedRecords = new AtomicLong(0);
    private long lastProcessedOffset = 0;
    private final Sheets sheetsService;
    private final Map<Integer, SheetRow> hashedRows;
    private final List<String> headers;
    private final ErrorOutputStream errorOutputStream;

    public GoogleSheetsObjectSink(GoogleSheetsAppConfig googleSheetsAppConfig, GoogleSheetsAppSyncConfig googleSheetsAppSyncConfig,
                                  Sheets sheetsService, List<SheetRow> sheetRows, List<String> primaryKeys, List<String> headers,
                                  ErrorOutputStream errorOutputStream) {
        this.googleSheetsAppConfig = googleSheetsAppConfig;
        this.googleSheetsAppSyncConfig = googleSheetsAppSyncConfig;
        this.sheetsService = sheetsService;
        this.hashedRows = Optional.ofNullable(sheetRows)
                .map(row -> row.stream().collect(Collectors.toMap(rowRef -> GoogleSheetUtils.getPrimaryKeysHash(rowRef.getValues(), primaryKeys), Function.identity())))
                .orElse(Maps.newHashMap());
        this.primaryKeys = primaryKeys;
        this.headers = headers;
        this.errorOutputStream = errorOutputStream;
    }

    @Override
    protected void writeRecords(List<DataSinkMessage> messages) {
        try {
            List<ValueRange> updateValueRanges = Lists.newArrayList();
            List<List<Object>> appendValues = Lists.newArrayList();
            for (DataSinkMessage message : messages) {
                LinkedHashMap<String, Object> rowValues = headers.stream().collect(Collectors.toMap(Function.identity(),
                        header -> GoogleSheetUtils.getSheetsValue(message.getRecord().getField(header)), (v1, v2) -> v1, LinkedHashMap::new));

                int primaryKeyHash = GoogleSheetUtils.getPrimaryKeysHash(rowValues, primaryKeys);
                if (hashedRows.containsKey(primaryKeyHash)) {
                    SheetRow sheetRow = hashedRows.get(primaryKeyHash);
                    updateValueRanges.add(new ValueRange().setValues(Collections.singletonList(new ArrayList<>(rowValues.values())))
                            .setRange(GoogleSheetUtils.getRange(googleSheetsAppSyncConfig.getObject().getObjectName(), sheetRow.getRowNo())));
                    hashedRows.remove(primaryKeyHash);
                } else {
                    appendValues.add(new ArrayList<>(rowValues.values()));
                }
            }
            if (CollectionUtils.isNotEmpty(updateValueRanges)) {
                BatchUpdateValuesRequest batchUpdateValuesRequest = new BatchUpdateValuesRequest().setData(updateValueRanges).setValueInputOption("USER_ENTERED");
                sheetsService.spreadsheets().values()
                        .batchUpdate(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()), batchUpdateValuesRequest).execute();
            }
            if (CollectionUtils.isNotEmpty(appendValues)) {
                ValueRange valueRange = new ValueRange().setValues(appendValues);
                sheetsService.spreadsheets()
                        .values().append(GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()),
                                googleSheetsAppSyncConfig.getObject().getObjectName(), valueRange).setValueInputOption("USER_ENTERED").execute();

            }
        } catch (Exception e) {
            handleGSheetsError(messages, e);
        }
        this.processedRecords.addAndGet(messages.size());
        this.lastProcessedOffset = Math.max(lastProcessedOffset, messages.get(messages.size() - 1).getOffset());
    }

    private void handleGSheetsError(List<DataSinkMessage> messages, Exception e) {
        log.error("Google Sheets append records failed for spreadsheet id {} and name {}",
                GoogleSheetUtils.getSpreadSheetId(googleSheetsAppConfig.getSpreadSheetId()),
                googleSheetsAppSyncConfig.getObject().getObjectName(), e);
        if (e instanceof GoogleJsonResponseException) {
            GoogleJsonResponseException gre = (GoogleJsonResponseException) e;
            for (DataSinkMessage message : messages) {
                this.errorOutputStream.writeFailedRecord(message, new ExternallyCategorizedError(gre.getStatusMessage(), gre.getContent()));
            }
            return;
        }
        for (DataSinkMessage message : messages) {
            this.errorOutputStream.writeFailedRecord(message, new UnclassifiedError(Optional.ofNullable(e.getMessage()).orElse("Unknown error")));
        }

    }

    public MessageSyncStats getSyncStats() {
        return new MessageSyncStats(processedRecords.get(), lastProcessedOffset);
    }

    @Override
    public long getMaxBufferedObjects() {
        return 10000;
    }
}
