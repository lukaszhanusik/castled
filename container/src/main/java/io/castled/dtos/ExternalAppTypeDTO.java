package io.castled.dtos;

import io.castled.apps.ExternalAppType;
import io.castled.commons.models.AccessType;
import io.castled.models.DataMappingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalAppTypeDTO {
    private ExternalAppType value;
    private String title;
    private AccessType accessType;
    private String logoUrl;
    private String docUrl;
    private long count;
    private DataMappingType mappingType;
}
