package io.castled.utils;

import com.google.common.collect.Lists;
import io.castled.models.AppFieldDetails;
import io.castled.schema.ParameterFieldDTO;
import io.castled.schema.SchemaFieldDTO;
import io.castled.schema.mapping.DestinationFieldGroup;
import io.castled.schema.mapping.ImportantParameterGroup;
import io.castled.schema.mapping.MiscellaneousFieldGroup;
import io.castled.schema.mapping.PrimaryKeyGroup;

import java.util.List;
import java.util.stream.Collectors;

public class MappingGroupUtil {

    public static ImportantParameterGroup constructImportantParameterGroup(List<AppFieldDetails> appFieldDetails) {
        ImportantParameterGroup importantParameterGroup = new ImportantParameterGroup();
        List<ParameterFieldDTO> importantParameters = Lists.newArrayList();
        appFieldDetails.forEach(appFieldDetail -> {
            importantParameters.add(new ParameterFieldDTO(appFieldDetail.getTitle(), appFieldDetail.getDescription(), appFieldDetail.getInternalName(),
                    appFieldDetail.getDisplayName(), appFieldDetail.getType(), appFieldDetail.isOptional()));
        });
        importantParameterGroup.setFields(importantParameters);
        return importantParameterGroup;
    }

    public static MiscellaneousFieldGroup constructMiscellaneousFieldGroup(boolean autoMap) {
        MiscellaneousFieldGroup miscellaneousFieldGroup = new MiscellaneousFieldGroup(autoMap);
        return miscellaneousFieldGroup;
    }

    public static DestinationFieldGroup constructDestinationFieldGroup(List<AppFieldDetails> appFieldDetailsList) {
        DestinationFieldGroup destinationFieldGroup = new DestinationFieldGroup();

        List<SchemaFieldDTO> optionalFields = Lists.newArrayList();
        appFieldDetailsList.stream().filter(appFieldDetails -> appFieldDetails.isOptional()).collect(Collectors.toList()).forEach(appFieldDetail -> {
            optionalFields.add(new SchemaFieldDTO(appFieldDetail.getInternalName(), appFieldDetail.getDisplayName(),
                    appFieldDetail.getType(), appFieldDetail.isOptional()));
        });

        List<SchemaFieldDTO> mandatoryFields = Lists.newArrayList();
        appFieldDetailsList.stream().filter(appFieldDetails -> !appFieldDetails.isOptional()).collect(Collectors.toList()).forEach(appFieldDetail -> {
            mandatoryFields.add(new SchemaFieldDTO(appFieldDetail.getInternalName(), appFieldDetail.getDisplayName(),
                    appFieldDetail.getType(), appFieldDetail.isOptional()));
        });

        destinationFieldGroup.setMandatoryFields(mandatoryFields);
        destinationFieldGroup.setOptionalFields(optionalFields);
        return destinationFieldGroup;
    }

    public static PrimaryKeyGroup constructPrimaryKeyGroup(List<AppFieldDetails> appFieldDetails) {
        PrimaryKeyGroup primaryKeyGroup = new PrimaryKeyGroup();
        List<SchemaFieldDTO> primaryKeys = Lists.newArrayList();
        appFieldDetails.forEach(appFieldDetail -> {
            primaryKeys.add(new SchemaFieldDTO(appFieldDetail.getInternalName(), appFieldDetail.getDisplayName(),
                    appFieldDetail.getType(), appFieldDetail.isOptional()));
        });
        primaryKeyGroup.setPrimaryKeys(primaryKeys);
        return primaryKeyGroup;
    }
}
