package com.wind.common;

/**
 * 日期格式化常量
 * 时间格式参见：https://blog.csdn.net/lilongsy/article/details/130130776
 *
 * @author wuxp
 * @date 2024-04-07 15:51
 **/
public final class WindDateFormatPatterns {

    private WindDateFormatPatterns() {
        throw new AssertionError();
    }

    public static final String ISO_8601_EXTENDED_DATETIME = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY = "yyyy";

}
