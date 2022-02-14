package io.castled.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
public class MustacheUtils {

    public static Map<String, Object> constructPayload(String payloadTemplate, Map<String, Object> inputMap) throws IOException {
        try {
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache m = mf.compile(new StringReader(payloadTemplate), "template.output");
            StringWriter writer = new StringWriter();
            m.execute(writer, inputMap).flush();
            return JsonUtils.jsonStringToMap(writer.toString());
        } catch (IOException ie) {
            log.error(String.format("Constructing payload from mustache template %s failed", payloadTemplate));
            throw ie;
        }
    }
}