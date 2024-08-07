package com.wind.mask;

import com.alibaba.fastjson2.JSON;
import com.wind.mask.annotation.Sensitive;
import com.wind.mask.masker.json.JsonStringMasker;
import com.wind.mask.masker.json.MapObjectMasker;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author wuxp
 * @date 2024-08-02 15:18
 **/
class ObjectDataMaskingUtilsTests {

    private DefaultObjectSanitizerDemo1 demo1;

    @BeforeEach
    void setup() {
        demo1 = new DefaultObjectSanitizerDemo1();
        demo1.setSensitiveMaps(ObjectMaskPrinterTests.buildSensitiveMaps());
        demo1.setSensitiveText(JSON.toJSONString(ObjectMaskPrinterTests.buildSensitiveMaps()));


        ObjectDataMaskingUtils.registerRule(DefaultObjectSanitizerDemo2.class,
                DefaultObjectSanitizerDemo2.Fields.sensitiveMaps2,
                Collections.singletonList("$.data.values[0].ak"),
                MapObjectMasker.class);

        ObjectDataMaskingUtils.registerRule(DefaultObjectSanitizerDemo2.class,
                MaskRule.mark(DefaultObjectSanitizerDemo2.Fields.sensitiveText2, Arrays.asList("$.data.values[0].ak", "$.data.ak"), JsonStringMasker.class)
        );
    }

    @Test
    void testRequiredMask() {
        Assertions.assertTrue(ObjectDataMaskingUtils.requiredSanitize(DefaultObjectSanitizerDemo1.class));
        Assertions.assertTrue(ObjectDataMaskingUtils.requiredSanitize(DefaultObjectSanitizerDemo2.class));
    }

    @Test
    void testMask() {
        DefaultObjectSanitizerDemo1 result = ObjectDataMaskingUtils.mask(demo1);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getSensitiveText().contains("***"));
    }

    @Test
    void testMask2() {
        DefaultObjectSanitizerDemo2 target = mockDemo2();
        DefaultObjectSanitizerDemo2 result2 = ObjectDataMaskingUtils.maskWithDeepCopy(target);
        Assertions.assertNotNull(result2);
        Assertions.assertTrue(result2.getSensitiveText2().contains("***"));
        Assertions.assertTrue(result2.getSensitiveText2().contains("***"));
        // clear sensitive rules
        ObjectDataMaskingUtils.clearClassRules(DefaultObjectSanitizerDemo2.class);
        result2 = ObjectDataMaskingUtils.maskWithDeepCopy(target);
        Assertions.assertNotNull(result2);
        Assertions.assertFalse(result2.getSensitiveText2().contains("***"));
    }

    private DefaultObjectSanitizerDemo2 mockDemo2() {
        DefaultObjectSanitizerDemo2 result = new DefaultObjectSanitizerDemo2();
        result.setSensitiveMaps2(ObjectMaskPrinterTests.buildSensitiveMaps());
        result.setSensitiveText2(JSON.toJSONString(ObjectMaskPrinterTests.buildSensitiveMaps()));
        return result;
    }

    @Data
    @Sensitive(sanitizer = ObjectMasker.class)
    static class DefaultObjectSanitizerDemo1 {

        @Sensitive(names = {"$.data.values[0].ak"}, sanitizer = MapObjectMasker.class)
        private Map<String, Object> sensitiveMaps;

        @Sensitive(names = {"$.data.values[0].ak", "$.data.ak"}, sanitizer = JsonStringMasker.class)
        private String sensitiveText;
    }

    @Data
    @FieldNameConstants
    static class DefaultObjectSanitizerDemo2 {

        private Map<String, Object> sensitiveMaps2;

        private String sensitiveText2;
    }
}
