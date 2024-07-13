package com.wind.office.core.formatter;

import com.wind.common.WindDateFormatPatterns;
import com.wind.common.exception.DefaultExceptionCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

/**
 * @author wuxp
 * @date 2024-04-07 14:45
 **/
class DefaultFormatterFactoryTests {

    @Test
    void testOfBool() {
        Formatter<Boolean> formatter = DefaultFormatterFactory.ofBool("启用", "禁用");
        Assertions.assertEquals("启用", formatter.print(Boolean.TRUE, Locale.CHINA));
        Assertions.assertEquals("禁用", formatter.print(Boolean.FALSE, Locale.CHINA));
    }

    @Test
    void testOfEnum() {
        Formatter<DefaultExceptionCode> formatter = DefaultFormatterFactory.ofEnum(DefaultExceptionCode.class);
        Assertions.assertEquals("请求不合法", formatter.print(DefaultExceptionCode.BAD_REQUEST, Locale.CHINA));
    }

    @Test
    void testOfDateTime() throws Exception {
        Formatter<TemporalAccessor> formatter = DefaultFormatterFactory.ofDateTime(WindDateFormatPatterns.YYYY_MM_DD_HH_MM_SS);
        String text = "2007-12-03 10:15:30";
        TemporalAccessor time = formatter.parse(text, Locale.CHINA);
        Assertions.assertEquals(text, formatter.print(time, Locale.CHINA));
    }
}
