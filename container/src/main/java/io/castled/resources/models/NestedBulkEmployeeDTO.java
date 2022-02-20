package io.castled.resources.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NestedBulkEmployeeDTO {

    private BulkEmployeeDTO list;
}
