package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class BulkEventUpdateRequest {

    private List<Map<String, Object>> events;
}
