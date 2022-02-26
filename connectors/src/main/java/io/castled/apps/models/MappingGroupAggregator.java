package io.castled.apps.models;

import com.google.common.collect.Lists;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.mapping.QuestionnaireGroupField;
import io.castled.schema.mapping.MappingGroup;
import io.castled.utils.MappingGroupUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class MappingGroupAggregator {

    @Getter
    private List<MappingGroup> mappingGroups;

    public static MappingGroupAggregator.Builder builder() {
        return new MappingGroupAggregator.Builder();
    }

    public static class Builder {

        private final MappingGroupAggregator mappingGroupAggregator = new MappingGroupAggregator(Lists.newArrayList());

        public Builder addPrimaryKeyFields(List<PrimaryKeyGroupField> primaryKeys) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.toPrimaryKeyFieldsGroup(primaryKeys));
            return this;
        }

        public Builder addQuestionnaireFields(List<QuestionnaireGroupField> questionnaireGroupFields) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.toQuestionnaireGroupDTO(questionnaireGroupFields));
            return this;
        }

        public Builder addFixedAppFields(List<FixedGroupAppField> fixedGroupAppFields) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.toFixedAppFieldsGroup(fixedGroupAppFields));
            return this;
        }

        public Builder addElasticAppFields(boolean autoMap) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.toElasticAppFieldsGroup(autoMap));
            return this;
        }

        public MappingGroupAggregator build() {
            return mappingGroupAggregator;
        }
    }
}
