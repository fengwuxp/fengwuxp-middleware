package com.wind.sensitive;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.wind.sensitive.annotation.Sensitive;
import com.wind.sensitive.sanitizer.json.JsonStringSanitizer;
import com.wind.sensitive.sanitizer.json.MapObjectSanitizer;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.wind.sensitive.ObjectSanitizePrinter.IdentityLimitPrinter.MAX_COLLECTION_SIZE;

/**
 * @author wuxp
 * @date 2024-03-11 13:36
 **/
class ObjectSanitizePrinterTests {

    private final ObjectSanitizePrinter printer = createPrinter();

    private ObjectSanitizePrinterExample example;

    @BeforeEach
    void setup() {
        example = new ObjectSanitizePrinterExample();
        example.setName("zhangs");
        example.setAk("AK_test");
        example.setCode("1002");
        example.setDesc("测试");
        example.setZed(true);
        example.setAge(11);
        example.setDepthObject(new HashMap<>());
    }

    private ObjectSanitizePrinter createPrinter() {
        List<SensitiveRuleGroup> ruleGroups = SensitiveRuleGroup.builder()
                .form(Map.class)
                .mark(ObjectSanitizer.ASTERISK)
                .build();
        return new ObjectSanitizePrinter(new SensitiveRuleRegistry(ruleGroups), Collections.singletonList(ObjectSanitizer.ASTERISK));
    }

    @Test
    void testSanitizePrimitiveArray() {
        Assertions.assertEquals("[1, 2, 3]", printer.sanitize(new byte[]{1, 2, 3}));
        Assertions.assertEquals("[[1, 2, 3], [2, 3, 4], [3, 4, 5]]", printer.sanitize(new byte[][]{{1, 2, 3}, {2, 3, 4}, {3, 4, 5}}));
        Assertions.assertEquals("[[[1, 3]], [[2, 4]]]", printer.sanitize(new byte[][][]{{{1, 3}}, {{2, 4}}}));
        Assertions.assertEquals("[false, true]", printer.sanitize(new boolean[]{false, true}));
        Assertions.assertEquals("[c, b]", printer.sanitize(new char[]{'c', 'b'}));
        Assertions.assertEquals("[1, 2, 3]", printer.sanitize(new int[]{1, 2, 3}));
        Assertions.assertEquals("[1, 2, 3]", printer.sanitize(new long[]{1L, 2L, 3L}));
        Assertions.assertEquals("[1.0, 2.0, 3.0]", printer.sanitize(new double[]{1.0, 2.0, 3.0}));
    }

    @Test
    void testSanitizeObject() {
        Map<String, Object> map = new HashMap<>();
        map.put("test1", 1);
        map.put("test2", "test");
        map.put("test3", false);
        map.put("example", example);
        example.depthObject.put("exampleMap", map);
        String result = printer.sanitize(map);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("@ref["));
    }

    @Test
    void testSanitizeCycleMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("test", 1);
        map.put("test2", example);
        map.put("cycle", map);
        String result = printer.sanitize(map);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("@ref["));
    }

    @Test
    void testSanitizeCycleObject() {
        example.setObjectSanitizePrinterExample(example);
        String result = printer.sanitize(example);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("@ref["));
    }

    @Test
    void testSanitizeCycleObject2() {
        example.setObjectSanitizePrinterExample(example);
        Map<String, Object> map = new HashMap<>();
        map.put("test2", example);
        map.put("cycle", map);
        String result = printer.sanitize(map);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("@ref["));
    }

    @Test
    void testConcurrency() throws Exception {
        Map<String, Object> map = new HashMap<>();
        List<String> texts = new ArrayList<>();
        map.put("texts", texts);
        map.put("test2", example);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        Runnable putRunnable = () -> {
            map.put("test1", texts);
            texts.add("2");
            map.put("test3", texts);
        };
        Runnable printRunnable = () -> Assertions.assertNotNull(printer.sanitize(map));
        int maxCount = 10000;
        List<Future<?>> futures = new ArrayList<>(maxCount);
        for (int i = 0; i < maxCount; i++) {
            futures.add(executorService.submit(putRunnable));
            futures.add(executorService.submit(printRunnable));
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    @Test
    void testSanitizeDepthObject() {
        Map<String, Object> depth1 = new HashMap<>();
        depth1.put("test2", example);
        example.setDepthObject(depth1);
        Map<String, Object> depth2 = new HashMap<>();
        depth1.put("depth2", depth2);
        Map<String, Object> depth3 = new HashMap<>();
        depth2.put("depth3", depth3);
        Map<String, Object> depth4 = new HashMap<>();
        depth3.put("depth4", depth4);
        ObjectSanitizePrinterExample objectSanitizePrinterExampleDepth1 = new ObjectSanitizePrinterExample();
        objectSanitizePrinterExampleDepth1.setAge(1);
        objectSanitizePrinterExampleDepth1.setAk("depth");
        example.setObjectSanitizePrinterExample(objectSanitizePrinterExampleDepth1);
        String result = printer.sanitize(example);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.contains("@ref["));
    }

    @Test
    void testPrintOverMaxSize() {
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        String[] array = new String[MAX_COLLECTION_SIZE + 1];
        for (int i = 0; i < MAX_COLLECTION_SIZE + 1; i++) {
            map.put(String.valueOf(i), i);
            list.add(String.valueOf(i));
            array[i] = String.valueOf(i);
        }
        Assertions.assertEquals("java.util.HashMap 对象的大小超过：128", printer.sanitize(map));
        Assertions.assertEquals("[Ljava.lang.Object; 对象的大小超过：128", printer.sanitize(list));
        Assertions.assertEquals("[Ljava.lang.String; 对象的大小超过：128", printer.sanitize(array));
    }

    @Test
    void testJavaFunctions() {
        Assertions.assertNotNull(printer.sanitize((Supplier<String>) () -> ""));
    }

    @Test
    void testMapObjects() {
        example.setSensitiveMaps(buildSensitiveMaps());
        String val = printer.sanitize(example);
        Assertions.assertTrue(val.contains("****"));
    }

    @Test
    void testJsonString() {
        example.setSensitiveText(JSON.toJSONString(buildSensitiveMaps()));
        String val = printer.sanitize(example);
        Assertions.assertTrue(val.contains("****"));
    }

    static Map<String, Object> buildSensitiveMaps() {
        Map<String, Object> sensitiveMaps = new HashMap<>();
        List<Map<String, String>> values = new ArrayList<>();
        Map<String, String> item = new HashMap<>();
        item.put("ak", "0001");
        values.add(item);
        sensitiveMaps.put("data", ImmutableMap.of("values", values));
        return sensitiveMaps;
    }

    public static class ExampleObjectSanitizer implements ObjectSanitizer<Object, String> {

        @Override
        public String sanitize(Object obj, Collection<String> keys) {
            return ObjectSanitizePrinter.ASTERISK.sanitize(obj);
        }
    }

    @Data
    @Sensitive(names = {"code", "ak"}, sanitizer = ExampleObjectSanitizer.class)
    static class ObjectSanitizePrinterExample {

        @Sensitive(sanitizer = ExampleObjectSanitizer.class)
        private String name;

        private String ak;

        private String code;

        private String desc;

        private int age;

        private boolean zed;

        private ObjectSanitizePrinterExample objectSanitizePrinterExample;

        private Map<String, Object> depthObject;

        @Sensitive(names = {"$.data.values[0].ak"}, sanitizer = MapObjectSanitizer.class)
        private Map<String, Object> sensitiveMaps;

        @Sensitive(names = {"$.data.values[0].ak", "$.data.ak"}, sanitizer = JsonStringSanitizer.class)
        private String sensitiveText;
    }
}
