package io.castled.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.BadRequestException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MustacheUtils {

    private static final char TEMPLATE_START = '{';
    private static final char TEMPLATE_END = '}';

    public static Map<String, Object> constructPayload(String mustacheTemplate, Map<String, Object> inputMap) throws JsonParseException {

        String cleanedTemplate = mustacheTemplate.replaceAll("\\s+", "");
        StringWriter writer = new StringWriter();
        for (int i = 0; i < cleanedTemplate.length(); i++) {
            if (cleanedTemplate.charAt(i) == TEMPLATE_START && cleanedTemplate.charAt(i + 1) == TEMPLATE_START) {
                StringWriter templateVariable = new StringWriter();
                for (int j = i + 2; j < cleanedTemplate.length(); j++) {
                    if (cleanedTemplate.charAt(j) == TEMPLATE_END && cleanedTemplate.charAt(j + 1) == TEMPLATE_END) {
                        Object templateValue = inputMap.get(templateVariable.toString());
                        if (templateValue instanceof String) {
                            writer.append(StringUtils.quoteText((String) templateValue));
                        } else {
                            writer.append(templateValue.toString());
                        }
                        i = j + 1;
                        break;
                    }
                    templateVariable.append(cleanedTemplate.charAt(j));
                }
                continue;
            }
            writer.append(cleanedTemplate.charAt(i));
        }
        return JsonUtils.jsonStringToMap(trimPayloadTemplate(writer.toString()));
    }

    public static List<String> getTemplateVariables(String mustacheTemplate) {
        String cleanedTemplate = mustacheTemplate.replaceAll("\\s+", "");
        List<String> templateVariables = Lists.newArrayList();
        for (int i = 0; i < cleanedTemplate.length(); i++) {
            if (i == cleanedTemplate.length() - 1) {
                return templateVariables;
            }
            if (cleanedTemplate.charAt(i) == TEMPLATE_START && cleanedTemplate.charAt(i + 1) == TEMPLATE_START) {
                StringWriter templateVariable = new StringWriter();
                for (int j = i + 2; j < cleanedTemplate.length(); j++) {
                    if (cleanedTemplate.charAt(j) == TEMPLATE_END && cleanedTemplate.charAt(j + 1) == TEMPLATE_END) {
                        templateVariables.add(templateVariable.toString());
                        i = j + 1;
                        break;
                    }
                    templateVariable.append(cleanedTemplate.charAt(j));
                }
            }
        }
        return templateVariables;
    }

    private static String trimPayloadTemplate(String templateString) {
        StringWriter trimmedString = new StringWriter();
        for (int i = 0; i < templateString.length(); i++) {
            if (i == templateString.length() - 1) {
                trimmedString.append(templateString.charAt(i));
                continue;
            }
            if (!(templateString.charAt(i) == '\"' && templateString.charAt(i + 1) == '\"')) {
                trimmedString.append(templateString.charAt(i));
            }
        }
        return trimmedString.toString();

    }

    public static void validateMustacheJson(String mustacheJson) {
        validatePayload(mustacheJson);
        List<String> templateVariables = getTemplateVariables(mustacheJson);
        Map<String, Object> valuesMap = templateVariables.stream().collect(Collectors.toMap(template -> template, template -> "dummy"));
        try {
            constructPayload(mustacheJson, valuesMap);
        } catch (Exception e) {
            if (ExceptionUtils.getRootCause(e) instanceof JsonParseException)
                throw new BadRequestException("Json Invalid");
        }
    }

    public static void validatePayload(String payloadTemplate) {
        try {
            new DefaultMustacheFactory().compile(new StringReader(payloadTemplate), "template.output");
        } catch (com.github.mustachejava.MustacheException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
