package com.wind.context.injection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.wind.common.exception.BaseException;
import com.wind.context.variable.annotations.ContextRequestIp;
import com.wind.context.variable.annotations.ContextTenantId;
import com.wind.context.variable.annotations.ContextUserId;
import com.wind.context.variable.annotations.ContextUserNme;
import com.wind.context.variable.annotations.ContextVariable;
import com.wind.context.variable.annotations.ContextVariableNames;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

class ContextAnnotationMethodParameterInjectorTest {


    private final ContextAnnotationMethodParameterInjector injector = new ContextAnnotationMethodParameterInjector(() -> ImmutableMap.of(
            ContextVariableNames.USER_ID, 1L,
            ContextVariableNames.USER_NAME, "张三",
            ContextVariableNames.TENANT_ID, 101L,
            ContextVariableNames.REQUEST_IP, "10.0.0.1",
            "example", "test",
            "age", 23
    ), ImmutableSet.of("com.wind.context"));

    @Test
    void testInjectObject() {
        Method method = ReflectionUtils.findMethod(Example.class, "exampleObject", ExampleRequest.class);
        ExampleRequest request = new ExampleRequest();
        injector.inject(method, new Object[]{request});
        Assertions.assertEquals(1L, request.id);
        Assertions.assertEquals(2L, request.tenantId);
        Assertions.assertEquals(101L, request.tenantId2);
        Assertions.assertEquals("10.0.0.1", request.ip);
        Assertions.assertEquals("张三", request.userName);
        Assertions.assertEquals("test", request.example);
        Assertions.assertEquals(23, request.age);
        Assertions.assertEquals(true, request.falg);
    }

    @Test
    void testInjectParameter() {
        Method method = ReflectionUtils.findMethod(Example.class, "exampleParameter", Long.class, String.class);
        Object[] arguments = {null, null};
        injector.inject(method, arguments);
        Assertions.assertEquals(1L, arguments[0]);
        Assertions.assertEquals("test", arguments[1]);
    }

    static class Example {

        public void exampleObject(ExampleRequest request) {

        }

        public void exampleParameter(@ContextUserId Long id, @ContextVariable(expression = "#example") String userName) {

        }

        public void exampleRequired(@ContextVariable(expression = "#userName_1") String userName) {

        }
    }

    @Data
    public static class ExampleRequest {

        @ContextUserId
        private Long id = 100L;

        @ContextTenantId
        private Long tenantId = 2L;

        @ContextTenantId(override = true)
        private Long tenantId2;

        @ContextRequestIp
        private String ip;

        @ContextUserNme
        private String userName = "李四";

        @ContextVariable(name = "example")
        private String example;

        @ContextVariable(expression = "#age")
        private int age;

        @ContextVariable(expression = "#age > 22")
        private Boolean falg;
    }
}