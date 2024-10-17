package com.wind.common.util;

import com.wind.common.locks.WindLock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author wuxp
 * @date 2024-09-14 13:50
 **/
public class WindReflectUtilsTests {


    @Test
    void testResolveSuperInterfaceGenericType(){
        Type[] types = WindReflectUtils.resolveSuperInterfaceGenericType(new Example());
        Assertions.assertEquals(2,types.length);
    }

    static class Example implements Function<String, List<String>>{

        @Override
        public List<String> apply(String s) {
            return Collections.emptyList();
        }
    }
}
