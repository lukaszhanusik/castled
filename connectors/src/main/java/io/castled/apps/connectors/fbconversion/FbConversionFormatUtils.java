package io.castled.apps.connectors.fbconversion;

import io.castled.apps.connectors.fbconversion.client.dtos.CustomDataField;
import io.castled.apps.connectors.fbconversion.client.dtos.CustomerInfoField;
import io.castled.apps.connectors.fbconversion.client.dtos.ServerEventField;
import io.castled.exceptions.CastledRuntimeException;
import io.castled.utils.CastledEncryptionUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FbConversionFormatUtils {

    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static String formatValue(Object value, CustomerInfoField customerInfoField) {
        switch (customerInfoField) {
            case DB:
                return DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH).format((LocalDate)value);
            case EMAIL:
            case LN:
            case FN:
            case CT:
            case ST:
            case COUNTRY:
                return toNormalizedString((String) value);
            case GEN:
                return normalizeGender((String) value);
            case PHONE:
            case ZIP:
            case EXTERN_ID:
            case CLIENT_IP:
            case CLIENT_USER:
            case CLICK_ID:
            case BROWSER_ID:
            case SUB_ID:
            case LOGIN_ID:
            case LEAD_ID:
                return (String) value;

            default:
                throw new CastledRuntimeException("Invalid customer filed type!");
        }
    }

    public static String formatValue(Object value, CustomDataField customDataField) {
        switch (customDataField) {
            case VALUE:
            case PREDICTED_LTV:
                return DECIMAL_FORMAT.format((double) value);
            default:
                return (String) value;
        }
    }

    public static String formatValue(Object value, ServerEventField serverEventField) {
        switch (serverEventField) {
            case EVENT_NAME:
            case EVENT_TIME:
            case EVENT_ID:
            case EVENT_SOURCE_URL:
            case OPT_OUT:
            case ACTION_SOURCE:
            case DATA_PROC_OPT:
            case DATA_PROC_OPT_CT:
            case DATA_PROC_OPT_ST:
                return (String) value;
            default:
                throw new CastledRuntimeException("Invalid customer fieled type!");
        }
    }

    public static String normalizeGender(String gender) {
        String nGender = toNormalizedString(gender);
        if ("male".contains(nGender)) {
            return "m";
        } else {
            return "f";
        }
    }

    public static String hashValue(String value, CustomerInfoField customerInfoField) {
        if (customerInfoField.isHashable()) {
            return CastledEncryptionUtils.toSHA256String(value);
        } else {
            return value;
        }
    }

    private static String toNormalizedString(String value) {
        return value.trim().toLowerCase();
    }
}