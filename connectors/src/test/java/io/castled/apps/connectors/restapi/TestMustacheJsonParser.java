package io.castled.apps.connectors.restapi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.castled.utils.JsonUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TestMustacheJsonParser {

    @Test
    public void cleanMustacheJson() {
        MustacheJsonParser mustacheJsonParser = new MustacheJsonParser();
        String cleanedJson = mustacheJsonParser.cleanMustacheJson("{\n" +
                "\t\"id\": 1\n" +
                "}");
        Assert.assertEquals(cleanedJson, "{\"id\":1}");

        cleanedJson = mustacheJsonParser.cleanMustacheJson("{\n" +
                "\t\"id\": 1,\n" +
                "\t\"name\": \"{{name}}\"\n" +
                "}");
        System.out.println(cleanedJson);
        Assert.assertEquals(cleanedJson, "{\"id\":1,\"name\":{{name}}}");
    }

    @Test
    public void testRecordTemplate() {
        MustacheJsonParser mustacheJsonParser = new MustacheJsonParser();
        MustacheJsonParser.BulkMustacheTokenizedResponse tokenizedResponse = mustacheJsonParser.tokenizeBulkMustacheJson("{\n" +
                "\n" +
                "\t\"name\": \"name\",\n" +
                "\t\"list\": {\n" +
                "\t\t\"type\": \"dummy\",\n" +
                "\t\t\"employees\": [{\n" +
                "\t\t\t\"id\": \"{{id}}\"\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}", "list.employees");



        List<Map<String, Object>> inputDetails = Lists.newArrayList();
        inputDetails.add(ImmutableMap.of("id",1));
        inputDetails.add(ImmutableMap.of("id","abcd"));

        List<Map<String, Object>> transformedInput = Lists.newArrayList();
        for (Map<String, Object> inputDetail : inputDetails) {
            transformedInput.add(mustacheJsonParser.resolveTemplate(tokenizedResponse.getRecordTemplate(), inputDetail));
        }
        String finalJson = String.format("%s%s%s", tokenizedResponse.getArrayPrefix(), JsonUtils.objectToString(transformedInput),
                tokenizedResponse.getArraySuffix());
        Assert.assertEquals(finalJson,"{\"name\":\"name\",\"list\":{\"type\":\"dummy\",\"employees\":[{\"id\":1},{\"id\":\"abcd\"}]}}");

    }
}