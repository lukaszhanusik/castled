package io.castled.pipelines;

import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.apps.DataWriter;
import io.castled.apps.models.DataWriteRequest;
import io.castled.commons.models.AppSyncStats;
import io.castled.commons.models.PipelineSyncStats;
import io.castled.commons.streams.ErrorOutputStream;
import io.castled.core.IncessantRunner;
import io.castled.functionalinterfaces.Action;
import io.castled.services.PipelineService;
import io.castled.utils.TimeUtils;

import java.util.Optional;

@Singleton
public class MonitoredDataSink {

    private static class SyncStatsUpdateAction implements Action {

        private final Long pipelineRunId;
        private final PipelineSyncStats startingSyncStats;
        private final DataWriter dataWriter;
        private final DataWriteRequest dataWriteRequest;
        private PipelineSyncStats lastUpdatedStats;


        public SyncStatsUpdateAction(Long pipelineRunId, PipelineSyncStats startingSyncStats, DataWriter dataWriter,
                                     DataWriteRequest dataWriteRequest) {
            this.pipelineRunId = pipelineRunId;
            this.startingSyncStats = startingSyncStats;
            this.dataWriter = dataWriter;
            this.dataWriteRequest = dataWriteRequest;
            this.lastUpdatedStats = new PipelineSyncStats(startingSyncStats.getRecordsSynced(), startingSyncStats.getRecordsFailed(),
                    startingSyncStats.getRecordsSkipped(), startingSyncStats.getOffset());
        }

        @Override
        public void execute() {
            PipelineSyncStats pipelineSyncStats = getPipelineSyncStats(startingSyncStats, dataWriter.getSyncStats(), dataWriteRequest.getErrorOutputStream());
            PipelineSyncStats verifiedSyncStats = new PipelineSyncStats(Math.max(pipelineSyncStats.getRecordsSynced(), lastUpdatedStats.getRecordsSynced()),
                    Math.max(pipelineSyncStats.getRecordsFailed(), lastUpdatedStats.getRecordsFailed()),
                    Math.max(pipelineSyncStats.getRecordsSkipped(), lastUpdatedStats.getRecordsSkipped()),
                    pipelineSyncStats.getOffset());
            ObjectRegistry.getInstance(PipelineService.class).updateSyncStats(pipelineRunId, verifiedSyncStats);
            updateFirstDataSynced(dataWriteRequest.getExternalApp().getTeamId(), verifiedSyncStats);
            this.lastUpdatedStats = verifiedSyncStats;
        }

    }

    public PipelineSyncStats syncRecords(DataWriter dataWriter, PipelineSyncStats startingSyncStats,
                                         Long pipelineRunId, DataWriteRequest dataWriteRequest) throws Exception {
        IncessantRunner incessantRunner = new IncessantRunner(new SyncStatsUpdateAction(pipelineRunId, startingSyncStats, dataWriter, dataWriteRequest), TimeUtils.secondsToMillis(5));
        dataWriter.writeRecords(dataWriteRequest);
        incessantRunner.shutdown(TimeUtils.minutesToMillis(1));
        PipelineSyncStats pipelineSyncStats = getPipelineSyncStats(startingSyncStats, dataWriter.getSyncStats(), dataWriteRequest.getErrorOutputStream());
        updateFirstDataSynced(dataWriteRequest.getExternalApp().getTeamId(), pipelineSyncStats);
        return pipelineSyncStats;
    }

    private static void updateFirstDataSynced(Long userId, PipelineSyncStats pipelineSyncStats) {
        /*
        if (ObjectRegistry.getInstance(UsersCache.class).getValue(userId).getFirstSyncTs() == null
                && pipelineSyncStats.getRecordsSynced() > 0) {
            ObjectRegistry.getInstance(UsersService.class).markFirstDataSynced(userId);
        }
        */
    }

    private static PipelineSyncStats getPipelineSyncStats(PipelineSyncStats startingSyncStats, AppSyncStats appSyncStats,
                                                          ErrorOutputStream errorOutputStream) {

        PipelineSyncStats pipelineSyncStats = Optional.ofNullable(appSyncStats).map(statsRef ->
                        new PipelineSyncStats(statsRef.getRecordsProcessed() - (errorOutputStream.getFailedRecords().get() + statsRef.getRecordsSkipped()),
                                errorOutputStream.getFailedRecords().get(), statsRef.getRecordsSkipped(), Math.min(statsRef.getOffset(),
                                Optional.ofNullable(errorOutputStream.getFirstFailedMessageId()).map(messageId -> messageId - 1).orElse(Long.MAX_VALUE))))
                .orElse(new PipelineSyncStats(0, 0, 0, 0));

        return new PipelineSyncStats(pipelineSyncStats.getRecordsSynced() + startingSyncStats.getRecordsSynced(),
                pipelineSyncStats.getRecordsFailed() + startingSyncStats.getRecordsFailed(),
                pipelineSyncStats.getRecordsSkipped() + startingSyncStats.getRecordsSkipped(),
                Math.max(pipelineSyncStats.getOffset(), startingSyncStats.getOffset()));

    }
}
