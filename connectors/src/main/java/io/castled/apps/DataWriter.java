package io.castled.apps;

import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;

public interface DataWriter {

    void writeRecords(DataWriteRequest dataWriteRequest) throws Exception;

    AppSyncStats getSyncStats();
}
