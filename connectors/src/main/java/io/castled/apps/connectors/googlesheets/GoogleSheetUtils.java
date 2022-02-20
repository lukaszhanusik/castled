package io.castled.apps.connectors.googlesheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.castled.commons.models.ServiceAccountDetails;
import io.castled.schema.models.Field;
import io.castled.utils.JsonUtils;
import io.castled.utils.ListUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GoogleSheetUtils {

    private static final Pattern SPREADSHEET_PATTERN = Pattern.compile("https://docs.google.com/spreadsheets(/u/[0-9]+)?/d/([^/]+)/edit.*");

    public static Sheets getSheets(ServiceAccountDetails serviceAccountDetails) throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredentials googleCredentials;
        List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        try (InputStream serviceAccountStream = new ByteArrayInputStream(JsonUtils.objectToByteArray(serviceAccountDetails))) {
            googleCredentials = GoogleCredentials.fromStream(serviceAccountStream).createScoped(SCOPES);
        }
        return new Sheets.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(googleCredentials))
                .setApplicationName("Castled")
                .build();
    }

    public static String getSpreadSheetId(String spreadsheetUrl) {

        Matcher matcher = SPREADSHEET_PATTERN.matcher(spreadsheetUrl);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return spreadsheetUrl;
    }

    public static boolean validSpreadSheetUrl(String spreadsheetUrl) {
        return SPREADSHEET_PATTERN.matcher(spreadsheetUrl).find();
    }

    public static List<SheetRow> getRows(Sheets sheetsService, String spreadSheetId, String sheetName) throws IOException {
        List<List<Object>> sheetEntries = sheetsService.spreadsheets().values()
                .get(spreadSheetId, sheetName).execute().getValues();
        if (CollectionUtils.isEmpty(sheetEntries) || sheetEntries.size() == 1) {
            return null;
        }
        List<String> headers = sheetEntries.get(0).stream().map(String::valueOf).collect(Collectors.toList());
        List<List<Object>> rowValues = sheetEntries.subList(1, sheetEntries.size());
        List<SheetRow> sheetRows = Lists.newArrayList();
        for (int i = 0; i < rowValues.size(); i++) {
            sheetRows.add(new SheetRow(i + 2, getSheetValues(headers, rowValues.get(i))));
        }
        return sheetRows;
    }

    private static LinkedHashMap<String, Object> getSheetValues(List<String> headers, List<Object> rowValue) {
        LinkedHashMap<String, Object> sheetValues = Maps.newLinkedHashMap();
        for (int index = 0; index < headers.size(); index++) {
            sheetValues.put(headers.get(index), ListUtils.nullOnIndexOutOfBounds(rowValue, index));
        }
        return sheetValues;
    }

    public static Integer getPrimaryKeysHash(Map<String, Object> row, List<String> primaryKeys) {
        return primaryKeys.stream().map(row::get).collect(Collectors.toList()).hashCode();
    }

    public static Object getSheetsValue(Field field) {
        if (field == null) {
            return "";
        }
        switch (field.getSchema().getType()) {
            case DATE:
                LocalDate localDate = (LocalDate) field.getValue();
                return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            case TIMESTAMP:
                LocalDateTime localDateTime = (LocalDateTime) field.getValue();
                return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case ZONED_TIMESTAMP:
                ZonedDateTime zonedDateTime = (ZonedDateTime) field.getValue();
                return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            case TIME:
                LocalTime localTime = (LocalTime) field.getValue();
                return localTime.format(DateTimeFormatter.ISO_TIME);
            case INT:
            case LONG:
            case SHORT:
                return String.valueOf(field.getValue());
            default:
                return field.getValue();
        }
    }

    public static String getRange(String sheetName, long rowNo) {
        return String.format("%s!%d:%d", sheetName, rowNo, rowNo);
    }
}
