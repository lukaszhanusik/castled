package io.castled.apps.connectors.Iterable;

import io.castled.apps.connectors.Iterable.client.dtos.CatalogItemField;
import io.castled.exceptions.CastledRuntimeException;
import org.apache.commons.lang.StringUtils;

public class IterableValidationUtils {

    public static void validateValue(String value, String fieldName) {
        if (CatalogItemField.ITEM_ID.getName().equals(fieldName)) {
            String temp = value.replace('-', 'x');
            if (!StringUtils.isAlphanumeric(temp)) {
                throw new CastledRuntimeException("Only alphanumeric characters and dashes for allowed for Catalog itemId!");
            }
        }
    }
}
