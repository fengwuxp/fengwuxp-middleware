package com.wind.script.j2v8;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class JavaScriptExecutorTest {


    @BeforeEach
    public void setup() {
        JavaScriptExecutor.configure("Array.prototype.includes = function (searchElement, fromIndex) {\n"
                + "  return this.indexOf(searchElement, fromIndex) >= 0\n"
                + "}");
    }

    @Test
    public void testExecuteFunctionSupportJavaUseClosure() {
        JsExecJavaMethodInvoker invoker = new JsExecJavaMethodInvoker() {
            @Override
            protected Object execInternal(String execMethodId, String parameter) {
                return JSON.parse(parameter);
            }
        };
        String json = JavaScriptExecutor.executeFunctionSupportJavaUseClosure(
                "function(name,age){return $java.exec('test',JSON.stringify({name:name,age:age}))}", invoker, "张三", 22);
        Assertions.assertEquals(json, "{\"name\":\"张三\",\"age\":22}");
    }

    @Test
    public void testExecuteFunctionThrowError() {
        try {
            JavaScriptExecutor.executeFunctionUseClosure("function(name,age){"
                    + " throw new Error('test error')"
                    + "  }", "张三", 22);
        } catch (Exception exception) {
            Assertions.assertEquals(exception.getMessage(), "test error");
        }
    }

    @Test
    public void testExecuteFunctionReturnMap() {
        String functionCode = "function converter(json,num) {\n"
                + "    const text = json.text;\n"
                + "    const name = json.name;\n"
                + "    const age = json.age;\n"
                + "    return {\n"
                + "        age:num,\n"
                + "        name: text + \"_\" + name + \"_\" + age,\n"
                + "        b:1,\n"
                + "        c:{\n"
                + "            h:2\n"
                + "        }\n"
                + "    }\n"
                + "}";
        Map<String, Object> map = new HashMap<>(4);
        map.put("text", "测试");
        map.put("name", "张三");
        map.put("age", 22);
        Map<String, Object> result = JavaScriptExecutor.executeFunctionUseClosure(functionCode, map, 22);
        Assertions.assertNotNull(result);
        UserDemo userDemo = JavaScriptExecutor.executeFunctionUseClosure(functionCode, UserDemo.class, map, 22);
        Assertions.assertNotNull(userDemo);
        Assertions.assertEquals(userDemo.name, "测试_张三_22");
        userDemo = JavaScriptExecutor.executeFunctionUseClosure(functionCode, new ParameterizedTypeReference<UserDemo>() {
        }, map, 22);
        Assertions.assertNotNull(userDemo);
        Assertions.assertEquals(userDemo.name, "测试_张三_22");
    }

    @Data
    static class UserDemo {

        private String name;

        private String age;
    }

    @Test
    public void testExecuteFunctionReturnList() {
        String functionCode = "function converter(val) {\n"
                + "   return val; \n"
                + "}";
        List<Integer> integers = Arrays.asList(1, 2, 3);
        List<Integer> integers1 = JavaScriptExecutor.executeFunctionUseClosure(functionCode, integers);
        Assertions.assertEquals(integers1, integers);

        List<Map<String, Object>> objects = Arrays.asList(ImmutableMap.of("a", 1), ImmutableMap.of("a", 2));
        List<Map<String, Object>> objects1 = JavaScriptExecutor.executeFunctionUseClosure(functionCode, objects);
        Assertions.assertEquals(objects1.get(0).get("a"), objects.get(0).get("a"));
    }

    @Test
    public void testExecuteFunctionReturnSimpleType() {
        String functionCode = "function converter(val) {\n"
                + "   return val; \n"
                + "}";
        int integer = JavaScriptExecutor.executeFunctionUseClosure(functionCode, 1);
        Assertions.assertEquals(integer, 1);
        boolean aBoolean = JavaScriptExecutor.executeFunctionUseClosure(functionCode, false);
        Assertions.assertFalse(aBoolean);
        String text = JavaScriptExecutor.executeFunctionUseClosure(functionCode, "abc");
        Assertions.assertEquals(text, "abc");
    }

    @Test
    public void testEsNextFeatures() {
        Assertions.assertTrue(() -> JavaScriptExecutor.executeFunctionUseClosure("function test(){return [1,2].includes(1);}"));
        Assertions.assertTrue(() -> JavaScriptExecutor.executeFunctionUseClosure("function test(){return `${1+2}` ==='3'; }"));
    }

    @Test
    public void testUseMomentJs() {
        String functionCode = "function test(){return moment('2022-04-15').format('YYYY-MM-DD')}";
        Assertions.assertEquals(JavaScriptExecutor.executeFunctionUseClosure(functionCode), "2022-04-15");
    }

    @Test
    public void testObjectAssign() {
        Map<String, Object> map = JavaScriptExecutor.executeFunctionUseClosure("function test(){return Object.assign({a:2},{a:1})}");
        Assertions.assertEquals(map.get("a"), 1);
    }

    @Test
    public void teatSubstring() {
        String functionCode = "function test(text){return text.startsWith('0')?text.substring(1):text}";
        Assertions.assertEquals(JavaScriptExecutor.executeFunctionUseClosure(functionCode, "012345"), "12345");
        Assertions.assertEquals(JavaScriptExecutor.executeFunctionUseClosure(functionCode, "12345"), "12345");
    }

    @Test
    public void testPrimitive() {
        String functionCode = "function test(a,b,flag){return flag? a + b:a-b}";
        int total = JavaScriptExecutor.executeFunctionUseClosure(functionCode, 1, 2, true);
        Assertions.assertEquals(total, 3);
        total = JavaScriptExecutor.executeFunctionUseClosure(functionCode, 1, 2, false);
        Assertions.assertEquals(total, -1);
    }

    @Test
    public void testFixUndefinedToNull() {
        Map<String, Object> result = JavaScriptExecutor.executeFunctionUseClosure("function test(){return {a:null,b:undefined,c:NaN}}");
        Assertions.assertNull(result.get("b"));
        Assertions.assertNotNull(JSON.toJSONString(result));
        Assertions.assertNull(JavaScriptExecutor.executeFunctionUseClosure("function test(){return undefined}"));
        Object v3 = JavaScriptExecutor.executeFunctionUseClosure("function test(){\n"
                + "  return {\n"
                + "    b: [\n"
                + "      {\n"
                + "        h: undefined, k: {\n"
                + "          t: [{h: 1, b: {c: undefined}}]\n"
                + "        }\n"
                + "      }\n"
                + "    ],\n"
                + "    c: {\n"
                + "      h: {\n"
                + "        k: undefined,\n"
                + "        z: null,\n"
                + "        f: {\n"
                + "          a: [{e: undefined, f: [{z: undefined}]}]\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}");
        Assertions.assertNotNull(JSON.toJSONString(v3));
    }

    @Test()
    public void testConcurrency() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<?>> futures = new ArrayList<>();
        int testTotal = 20;
        for (int i = 0; i < testTotal; i++) {
            Future<?> result = executorService.submit(() -> {
                Object resultValue = JavaScriptExecutor.executeFunctionUseClosure("function test(){return 1}");
                Assertions.assertNotNull(resultValue);
            });
            futures.add(result);
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    @Test
    public void testPrimitive1() {
        String functionCode = "function test(){return Array.from(new Set([\"1\",'2','2'])).join(\",\")}";
        String result = JavaScriptExecutor.executeFunctionUseClosure(functionCode);
        Assertions.assertEquals(result, "1,2");
    }
}