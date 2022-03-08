package io.castled.apps.connectors.fbconversion;

import io.castled.OptionsReferences;
import io.castled.apps.syncconfigs.BaseAppSyncConfig;
import io.castled.forms.*;
import lombok.Getter;

@Getter
public class FbConversionSyncConfig  extends BaseAppSyncConfig {

    @FormField(type = FormFieldType.DROP_DOWN, schema = FormFieldSchema.ENUM, title = "Action Source", description = "Specify where your conversions occurred, " +
            "if your warehouse table has a column for this that will override this config if used for mapping", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.ACTION_SOURCE_OPTIONS, type = OptionsRefType.STATIC), required = false)
    private String actionSource;

    @FormField(type = FormFieldType.TEXT_BOX, title = "Test Event Code", description = "Use to send test events to verify that they're received correctly by Facebook", schema = FormFieldSchema.STRING, group = MappingFormGroups.SYNC_MODE,
            required = false)
    private String testEventCode;

    @FormField(type = FormFieldType.RADIO_BOX, schema = FormFieldSchema.BOOLEAN, title = "Hashing Required?", description = "Decides whether Castled should hash Personally identifiable Information(PII).", group = MappingFormGroups.SYNC_MODE,
            optionsRef = @OptionsRef(value = OptionsReferences.HASHING_OPTIONS, type = OptionsRefType.STATIC), required = true, placeholder = "true")
    private boolean hashingRequired;
}
