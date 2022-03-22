package io.castled.apps.connectors.Iterable.client.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TemplateIdResponse {

    private List<TemplateId> templates;
}
