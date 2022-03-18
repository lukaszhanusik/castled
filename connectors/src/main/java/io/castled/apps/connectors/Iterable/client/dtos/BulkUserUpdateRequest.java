package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class BulkUserUpdateRequest {

    private List<Map<String, Object>> users;
}
