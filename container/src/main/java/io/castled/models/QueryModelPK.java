package io.castled.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryModelPK {
    private List<String> primaryKeys;
}
