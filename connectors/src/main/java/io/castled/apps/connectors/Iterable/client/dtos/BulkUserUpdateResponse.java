package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BulkUserUpdateResponse {

    private long successCount;
    private long failCount;
    private List<String> invalidEmails;
    private List<String> invalidUserIds;
}
