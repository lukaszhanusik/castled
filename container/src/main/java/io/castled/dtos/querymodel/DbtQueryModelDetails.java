package io.castled.dtos.querymodel;

import io.castled.models.QueryModelType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbtQueryModelDetails extends QueryModelDetails{

    public DbtQueryModelDetails(){
        super(QueryModelType.DBT_MODEL_SELECTOR);
    }
}
