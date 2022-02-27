package io.castled.apps.connectors.restapi;

import org.junit.Assert;
import org.junit.Test;

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
        mustacheJsonParser.getRecordTemplate("{\n" +
                "\n" +
                "\t\"name\": {{name}},\n" +
                "\t\"list\": {\n" +
                "\t\t\"type\": \"dummy\",\n" +
                "\t\t\"employees\": [{\n" +
                "\t\t\t\"id\": \"{{id}}\"\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}", "list.employees");

    }
}