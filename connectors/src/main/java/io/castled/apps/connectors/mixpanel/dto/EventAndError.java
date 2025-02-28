package io.castled.apps.connectors.mixpanel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class EventAndError {
    private Integer index;
    private Object insertId;
    private List<String> failureReasons;
}
