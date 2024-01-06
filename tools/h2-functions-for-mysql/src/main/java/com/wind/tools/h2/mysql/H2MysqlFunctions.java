package com.wind.tools.h2.mysql;

import com.wind.tools.h2.H2Function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * h2 Mysql 模式函数扩展支持
 *
 * @author wuxp
 * @date 2023-11-20 13:43
 **/
public final class H2MysqlFunctions {

    private static final Set<H2Function> DEFAULT_FUNCTIONS = new HashSet<>();

    static {
        DEFAULT_FUNCTIONS.add(new H2Function("FIND_IN_SET", "com.wind.tools.h2.mysql.H2MysqlFunctions.findInSet"));
    }

    private H2MysqlFunctions() {
        throw new AssertionError();
    }

    /**
     * FIND_IN_SET 支持
     *
     * @param keyWord 查找字符串
     * @param strSet  查找目标
     * @return 是否存在
     */
    public static Integer findInSet(String keyWord, String strSet) {
        if (keyWord == null || strSet == null) {
            return null;
        }
        if (strSet.isEmpty()) {
            return 0;
        }
        return Arrays.asList(strSet.split(",")).indexOf(keyWord) + 1;
    }

    public static Set<H2Function> getFunctions() {
        return DEFAULT_FUNCTIONS;
    }

}
