package io.castled.apps.connectors.restapi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.mustachejava.DefaultMustacheFactory;
import com.google.common.collect.Lists;
import io.castled.utils.JsonUtils;
import io.castled.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.BadRequestException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestApiTemplateParser {

    private static final char TEMPLATE_START = '{';
    private static final char TEMPLATE_END = '}';

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkMustacheTokenizedResponse {
        private String arrayPrefix;
        private String arraySuffix;
        private String recordTemplate;
    }

    public String resolveTemplateString(String mustacheTemplate, Map<String, Object> inputMap) {

        String cleanedTemplate = cleanMustacheJson(mustacheTemplate);
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
        return writer.toString();
    }

    public Map<String, Object> resolveTemplate(String mustacheTemplate, Map<String, Object> inputMap) {
        return JsonUtils.jsonStringToMap(resolveTemplateString(mustacheTemplate, inputMap));
    }

    public List<String> getTemplateVariables(String mustacheTemplate) {
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

    public BulkMustacheTokenizedResponse tokenizeBulkMustacheJson(String bulkMustacheJson, String arrayPath) throws InvalidTemplateException {

        String cleanedJson = cleanMustacheJson(bulkMustacheJson);
        List<String> templateVariables = getTemplateVariables(cleanedJson);
        Map<String, Object> valuesMap = templateVariables.stream().collect(Collectors.toMap(template -> template,
                template -> String.format("{{%s}}", template)));
        JsonNode jsonNode = JsonUtils.jsonStringToJsonNode(resolveTemplateString(cleanedJson, valuesMap));
        for (String token : arrayPath.split("\\.")) {
            jsonNode = jsonNode.get(token);
            if (jsonNode == null || jsonNode.isNull()) {
                throw new InvalidTemplateException(String.format("No object found at path %s", arrayPath));
            }
        }
        if (!jsonNode.isArray()) {
            throw new InvalidTemplateException(String.format("No Json Array found at path %s", arrayPath));
        }
        ArrayNode arrayNode = (ArrayNode) jsonNode;
        if (arrayNode.size() == 0) {
            throw new InvalidTemplateException(String.format("Json Array found at path %s cannot be empty", arrayPath));
        }
        if (arrayNode.size() > 1) {
            throw new InvalidTemplateException(String.format("Json Array found at path %s should contain exactly one element", arrayPath));
        }
        JsonNode templateNode = arrayNode.get(0);
        if (!templateNode.isObject()) {
            throw new InvalidTemplateException("Json Array found at path %s should be an array of json objects");
        }
        String templateJson = cleanMustacheJson(templateNode.toString());
        String templateArrayJson = cleanMustacheJson(arrayNode.toString());
        int templateArrayIndex = cleanedJson.indexOf(templateArrayJson);
        String templateArrayPrefix = cleanedJson.substring(0, templateArrayIndex);
        String templateArraySuffix = cleanedJson.substring(templateArrayIndex + templateArrayJson.length());
        return new BulkMustacheTokenizedResponse(templateArrayPrefix, templateArraySuffix, templateJson);

    }

    public String cleanMustacheJson(String mustacheJson) {
        mustacheJson = mustacheJson.replaceAll("\\s+", "");
        StringWriter cleanedJson = new StringWriter();
        for (int i = 0; i < mustacheJson.length(); i++) {
            if (mustacheJson.charAt(i) == '"') {
                if (i < mustacheJson.length() - 2 && mustacheJson.charAt(i + 1) == '{' && mustacheJson.charAt(i + 2) == '{') {
                    continue;
                }
                if (i >= 2 && mustacheJson.charAt(i - 1) == '}' && mustacheJson.charAt(i - 2) == '}') {
                    continue;
                }
            }
            cleanedJson.append(mustacheJson.charAt(i));
        }
        return cleanedJson.toString();

    }

    public void validateMustacheJson(String mustacheJson, RestApiAppSyncConfig restApiAppSyncConfig) throws InvalidTemplateException {
        validatePayload(mustacheJson);
        List<String> templateVariables = getTemplateVariables(mustacheJson);
        Map<String, Object> valuesMap = templateVariables.stream().collect(Collectors.toMap(template -> template,
                template -> String.format("{{%s}}", template)));
        try {
            resolveTemplate(mustacheJson, valuesMap);
        } catch (Exception e) {
            if (ExceptionUtils.getRootCause(e) instanceof JsonParseException)
                throw new InvalidTemplateException(String.format("Invalid Json template: %s", ExceptionUtils.getRootCauseMessage(e)));
        }
        if (restApiAppSyncConfig.isBulk()) {
            tokenizeBulkMustacheJson(mustacheJson, restApiAppSyncConfig.getJsonPath());
        }
    }


    public void validatePayload(String payloadTemplate) {
        try {
            new DefaultMustacheFactory().compile(new StringReader(payloadTemplate), "template.output");
        } catch (com.github.mustachejava.MustacheException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
