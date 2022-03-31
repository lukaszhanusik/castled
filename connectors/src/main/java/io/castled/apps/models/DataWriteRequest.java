package io.castled.apps.models;

import io.castled.apps.ExternalApp;
import io.castled.apps.syncconfigs.AppSyncConfig;
import io.castled.commons.streams.DataSinkMessageInputStream;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.commons.streams.MessageInputStream;;
import io.castled.models.CastledDataMapping;
import io.castled.models.QueryMode;
import io.castled.schema.models.RecordSchema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataWriteRequest {

    private ExternalApp externalApp;
    private DataSinkMessageInputStream messageInputStream;
    private ErrorOutputStream errorOutputStream;
    private AppSyncConfig appSyncConfig;
    private List<String> mappedFields;
    private RecordSchema objectSchema;
    private List<String> primaryKeys;
    private CastledDataMapping mapping;
    private QueryMode queryMode;
}
