package com.wind.sensitive;

import com.alibaba.fastjson2.JSON;
import com.wind.sensitive.annotation.Sensitive;
import com.wind.sensitive.sanitizer.json.JsonStringSanitizer;
import com.wind.sensitive.sanitizer.json.MapObjectSanitizer;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author wuxp
 * @date 2024-08-02 15:18
 **/
public class DefaultObjectSanitizerTests {

    private final DefaultObjectSanitizer sanitizer = new DefaultObjectSanitizer();

    private DefaultObjectSanitizerTests.Demo demo;

    @BeforeEach
    void setup() {
        demo = new Demo();
        demo.setSensitiveMaps(ObjectSanitizePrinterTests.buildSensitiveMaps());
        demo.setSensitiveText(JSON.toJSONString(ObjectSanitizePrinterTests.buildSensitiveMaps()));
    }

    @Test
    void testRequiredSanitize(){
        Assertions.assertTrue(sanitizer.requiredSanitize(Demo.class));
    }

    @Test
    void testSanitize() {
        Object result = sanitizer.sanitize(demo);
        Assertions.assertTrue(demo.getSensitiveText().contains("***"));
    }

    @Data
    @Sensitive(names = {}, sanitizer = ObjectSanitizer.class)
    static class Demo {

        @Sensitive(names = {"$.data.values[0].ak"}, sanitizer = MapObjectSanitizer.class)
        private Map<String, Object> sensitiveMaps;

        @Sensitive(names = {"$.data.values[0].ak", "$.data.ak"}, sanitizer = JsonStringSanitizer.class)
        private String sensitiveText;
    }
}
