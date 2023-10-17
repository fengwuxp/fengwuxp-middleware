package com.wind.common.exception;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class AssertUtilsTest {

    @Test
    void testAssertUtils() {
        AssertUtils.isTrue(true, "isTrue error");
        AssertUtils.isFalse(false, "isFalse error");
        AssertUtils.notNull(new Object(), "notNull error");
        AssertUtils.isNull(null, "isNull error");
        AssertUtils.notEmpty(new String[]{"1"}, "notEmpty error");
        AssertUtils.notEmpty(ImmutableSet.of("1", "2"), "notEmpty error");
        AssertUtils.notEmpty(ImmutableMap.of("1", "1"), "notEmpty error");
        AssertUtils.hasText(" hasText ", "hasText error");
        AssertUtils.hasLength(" hasLength ", "hasText error");
        AssertUtils.doesNotContain(" hasLength ", "zzz", "doesNotContain error");
        AssertUtils.isAssignable(Map.class, ImmutableMap.class, "isAssignable error");
        AssertUtils.isInstanceOf(Map.class, ImmutableMap.of("1", "2"), "isInstanceOf error");
        AssertUtils.noNullElements(ImmutableSet.of("1", "2"), "noNullElements error");
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> AssertUtils.state(false, () -> BaseException.unAuthorized("stateException")));
        Assertions.assertEquals(exception.getMessage(), "stateException");
    }

}