package io.castled.apps.models;

import com.google.common.collect.Lists;
import io.castled.models.AppFieldDetails;
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

        public Builder addPrimaryKeys(List<AppFieldDetails> primaryKeys) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.constructPrimaryKeyGroup(primaryKeys));
            return this;
        }

        public Builder addImportantParameters(List<AppFieldDetails> importantParameters) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.constructImportantParameterGroup(importantParameters));
            return this;
        }

        public Builder addDestinationFields(List<AppFieldDetails> destinationFields) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.constructDestinationFieldGroup(destinationFields));
            return this;
        }

        public Builder addMiscellaneousFields(boolean autoMap) {
            mappingGroupAggregator.getMappingGroups().add(MappingGroupUtil.constructMiscellaneousFieldGroup(autoMap));
            return this;
        }

        public MappingGroupAggregator build() {
            return mappingGroupAggregator;
        }
    }
}
