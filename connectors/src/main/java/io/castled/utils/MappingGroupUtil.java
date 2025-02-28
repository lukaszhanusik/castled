package io.castled.utils;

import com.google.common.collect.Lists;
import io.castled.mapping.FixedGroupAppField;
import io.castled.mapping.PrimaryKeyGroupField;
import io.castled.mapping.QuestionnaireGroupField;
import io.castled.schema.ParameterFieldDTO;
import io.castled.schema.SchemaFieldDTO;
import io.castled.schema.mapping.ElasticAppFieldsGroup;
import io.castled.schema.mapping.FixedAppFieldsGroup;
import io.castled.schema.mapping.PrimaryKeyFieldsGroup;
import io.castled.schema.mapping.QuestionnaireFieldsGroup;
import io.castled.schema.models.SchemaType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MappingGroupUtil {

    public static QuestionnaireFieldsGroup toQuestionnaireGroupDTO(List<QuestionnaireGroupField> mappingGroupFieldDetails) {
        QuestionnaireFieldsGroup importantParameterGroup = new QuestionnaireFieldsGroup();
        List<ParameterFieldDTO> importantParameters = Lists.newArrayList();
        mappingGroupFieldDetails.forEach(appFieldDetail -> {
            importantParameters.add(new ParameterFieldDTO(appFieldDetail.getTitle(), appFieldDetail.getDescription(), appFieldDetail.getName(),
                    appFieldDetail.getDisplayName(), SchemaType.STRING.getDisplayName(), appFieldDetail.isOptional()));
        });
        importantParameterGroup.setFields(importantParameters);
        return importantParameterGroup;
    }

    public static ElasticAppFieldsGroup toElasticAppFieldsGroup(String title, String description, boolean autoMap, boolean pkRequired) {
        ElasticAppFieldsGroup elasticAppFieldsGroup = new ElasticAppFieldsGroup(autoMap, pkRequired);
        Optional.ofNullable(title).ifPresent(elasticAppFieldsGroup::setTitle);
        Optional.ofNullable(description).ifPresent(elasticAppFieldsGroup::setDescription);
        return elasticAppFieldsGroup;
    }

    public static FixedAppFieldsGroup toFixedAppFieldsGroup(String title, String description, List<FixedGroupAppField> fixedGroupAppFields) {
        FixedAppFieldsGroup fixedAppFieldsGroup = new FixedAppFieldsGroup();
        Optional.ofNullable(title).ifPresent(fixedAppFieldsGroup::setTitle);
        Optional.ofNullable(description).ifPresent(fixedAppFieldsGroup::setDescription);

        List<SchemaFieldDTO> optionalFields = Lists.newArrayList();
        fixedGroupAppFields.stream().filter(FixedGroupAppField::isOptional).collect(Collectors.toList()).forEach(appFieldDetail -> {
            optionalFields.add(new SchemaFieldDTO(appFieldDetail.getName(), appFieldDetail.getDisplayName(),
                    SchemaType.STRING.getDisplayName(), appFieldDetail.isOptional()));
        });

        List<SchemaFieldDTO> mandatoryFields = Lists.newArrayList();
        fixedGroupAppFields.stream().filter(appFieldDetails -> !appFieldDetails.isOptional()).collect(Collectors.toList()).forEach(appFieldDetail -> {
            mandatoryFields.add(new SchemaFieldDTO(appFieldDetail.getName(), appFieldDetail.getDisplayName(),
                    SchemaType.STRING.getDisplayName(), appFieldDetail.isOptional()));
        });

        fixedAppFieldsGroup.setMandatoryFields(mandatoryFields);
        fixedAppFieldsGroup.setOptionalFields(optionalFields);
        return fixedAppFieldsGroup;
    }

    public static FixedAppFieldsGroup toFixedAppFieldsGroup(List<FixedGroupAppField> fixedGroupAppFields) {
        return toFixedAppFieldsGroup(null, null, fixedGroupAppFields);
    }

    public static PrimaryKeyFieldsGroup toPrimaryKeyFieldsGroup(List<PrimaryKeyGroupField> mappingGroupFieldDetails) {
        PrimaryKeyFieldsGroup primaryKeyGroup = new PrimaryKeyFieldsGroup();
        List<SchemaFieldDTO> primaryKeys = Lists.newArrayList();
        mappingGroupFieldDetails.forEach(appFieldDetail -> {
            primaryKeys.add(new SchemaFieldDTO(appFieldDetail.getName(), appFieldDetail.getDisplayName(),
                    SchemaType.STRING.getDisplayName(), appFieldDetail.isOptional()));
        });
        primaryKeyGroup.setPrimaryKeys(primaryKeys);
        return primaryKeyGroup;
    }
}
