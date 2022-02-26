package io.castled.apps.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.castled.schema.models.RecordSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
public class ExternalAppSchema {

    private RecordSchema appSchema;
    private PrimaryKeyEligibles pkEligibles;

    public ExternalAppSchema(RecordSchema appSchema, List<String> pkEligibles) {
        this.appSchema = appSchema;
        this.pkEligibles = PrimaryKeyEligibles.eligibles(pkEligibles);
    }

    public ExternalAppSchema(RecordSchema appSchema) {
        this.appSchema = appSchema;
    }
}
