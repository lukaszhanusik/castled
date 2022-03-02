package io.castled.warehouses.connectors.bigquery;

import com.google.cloud.bigquery.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.ObjectRegistry;
import io.castled.constants.ConnectorExecutionConstants;
import io.castled.exceptions.connect.ConnectException;
import io.castled.exceptions.connect.ConnectionError;
import io.castled.models.QueryResults;
import io.castled.schema.models.RecordSchema;
import io.castled.warehouses.BaseWarehouseConnector;
import io.castled.warehouses.TableProperties;
import io.castled.warehouses.WarehouseDataPoller;
import io.castled.warehouses.WarehouseSyncFailureListener;
import io.castled.warehouses.connectors.bigquery.daos.BQSnapshotTrackerDAO;
import io.castled.warehouses.connectors.bigquery.gcp.GcpClientFactory;
import io.castled.warehouses.models.WarehousePollContext;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@Slf4j
public class BigQueryConnector extends BaseWarehouseConnector<BigQueryWarehouseConfig> {

    private final GcpClientFactory gcpClientFactory;
    private final BQSnapshotTrackerDAO bqSnapshotTrackerDAO;

    @Inject
    public BigQueryConnector(GcpClientFactory gcpClientFactory, Jdbi jdbi) {
        this.gcpClientFactory = gcpClientFactory;
        this.bqSnapshotTrackerDAO = jdbi.onDemand(BQSnapshotTrackerDAO.class);
    }

    @Override
    public void testConnectionForDataPoll(BigQueryWarehouseConfig config) throws ConnectException {
        BigQuery bigQuery = this.gcpClientFactory.getBigQuery(config.getServiceAccount(),
                config.getProjectId());
        String blobName = "castled_" + UUID.randomUUID();
        Bucket bucket = null;
        try {
            BigQueryUtils.getOrCreateDataset(ConnectorExecutionConstants.CASTLED_CONTAINER, bigQuery, config.getLocation());
            BigQueryUtils.listTables(ConnectorExecutionConstants.CASTLED_CONTAINER, bigQuery);
            Storage storage = this.gcpClientFactory.getGcsClient(config.getServiceAccount(),
                            config.getProjectId())
                    .getStorage();
            bucket = storage.get(config.getBucketName());
            if (bucket == null) {
                throw new ConnectException(ConnectionError.INVALID_STORAGE, String.format("Bucket %s not found", config.getBucketName()));
            }
            if (!bucket.getLocation().equalsIgnoreCase(config.getLocation())) {
                throw new ConnectException(ConnectionError.INVALID_STORAGE,
                        String.format("GCS bucket %s needs to be created on the configured location %s", config.getBucketName(), config.getLocation()));
            }

            bucket.create(blobName, "test".getBytes(StandardCharsets.UTF_8));
            for (Blob blob : bucket.list(Storage.BlobListOption.prefix(blobName)).getValues()) {
                if (blob.getName().equals(blobName)) {
                    blob.downloadTo(new ByteArrayOutputStream());
                }
            }
            Optional.ofNullable(storage.get(config.getBucketName()))
                    .orElseThrow(() -> new ConnectException(ConnectionError.INVALID_STORAGE, "Bucket not found"));
        } catch (Exception e) {
            if (bucket != null) {
                tryDeleteBlob(blobName, bucket);
            }
            log.warn("Test connection failed for Big query service account {}", config.getServiceAccount(), e);
            throw new ConnectException(ConnectionError.UNKNOWN, e.getMessage());
        }
    }

    private void tryDeleteBlob(String blobName, Bucket bucket) {
        try {
            for (Blob blob : bucket.list(Storage.BlobListOption.prefix(blobName)).getValues()) {
                if (blob.getName().equals(blobName)) {
                    blob.downloadTo(new ByteArrayOutputStream());
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete blob {}", blobName, e);
        }
    }

    @Override
    public WarehouseDataPoller getDataPoller() {
        return ObjectRegistry.getInstance(BigQueryDataPoller.class);
    }

    @Override
    public RecordSchema getQuerySchema(BigQueryWarehouseConfig bigQueryWarehouseConfig, String query) throws Exception {
        String limitedQuery = String.format("select * from (%s) limit 0", query);
        BigQuery bigQuery = gcpClientFactory.getBigQuery(bigQueryWarehouseConfig.getServiceAccount(),
                bigQueryWarehouseConfig.getProjectId());
        TableResult tableResult = bigQuery.query(QueryJobConfiguration.newBuilder(limitedQuery).build());
        return BigQueryUtils.bqSchemaToConnectSchema(tableResult.getSchema());
    }

    @Override
    public WarehouseSyncFailureListener syncFailureListener(WarehousePollContext warehousePollContext) throws Exception {
        return new BQSyncFailureListener(warehousePollContext);
    }

    @Override
    public TableProperties getSnapshotTableProperties(List<String> recordIdKeys) {
        return null;
    }

    @Override
    public void restartPoll(String pipelineUUID, BigQueryWarehouseConfig config) {
        BigQuery bigQuery = gcpClientFactory.getBigQuery(config.getServiceAccount(),
                config.getProjectId());
        BQSnapshotTracker bqSnapshotTracker = bqSnapshotTrackerDAO.getSnapshotTracker(pipelineUUID);
        if (bqSnapshotTracker.getCommittedSnapshot() != null) {
            bigQuery.delete(TableId.of(ConnectorExecutionConstants.CASTLED_CONTAINER, bqSnapshotTracker.getCommittedSnapshot()));
        }
    }

    @Override
    public QueryResults previewQuery(String query, BigQueryWarehouseConfig bigQueryWarehouseConfig, int maxRows) throws Exception {
        String limitedQuery = String.format("select * from (%s) limit %d", query, maxRows);
        BigQuery bigQuery = gcpClientFactory.getBigQuery(bigQueryWarehouseConfig.getServiceAccount(),
                bigQueryWarehouseConfig.getProjectId());

        TableResult tableResult = bigQuery.query(QueryJobConfiguration.newBuilder(limitedQuery).build());
        List<String> headers = BigQueryUtils.fieldNames(tableResult.getSchema());
        List<List<String>> rows = Lists.newArrayList();
        do {
            for (FieldValueList fieldValueList : tableResult.getValues()) {
                int index = 0;
                List<String> row = Lists.newArrayList();
                for (FieldValue fieldValue : fieldValueList) {
                    if (fieldValue.getValue() == null) {
                        row.add(null);
                    } else {
                        row.add(BigQueryUtils.parseFieldValue(fieldValue.getValue(), tableResult.getSchema().getFields().get(index).getType()));
                    }
                    index++;
                }
                rows.add(row);
            }
        } while ((tableResult = tableResult.getNextPage()) != null);
        return new QueryResults(headers, rows);
    }

    @Override
    public Class<BigQueryWarehouseConfig> getConfigType() {
        return BigQueryWarehouseConfig.class;
    }

    public BigQueryWarehouseConfig filterRestrictedConfigDetails(BigQueryWarehouseConfig bigQueryWarehouseConfig) {
        return bigQueryWarehouseConfig;
    }
}
