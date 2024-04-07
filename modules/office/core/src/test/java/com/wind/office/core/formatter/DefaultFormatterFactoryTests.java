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
    void testOfDateTime() {
        Formatter<TemporalAccessor> formatter = DefaultFormatterFactory.ofDateTime(WindDateFormatPatterns.YYYY_MM_DD_HH_MM_SS);
        Assertions.assertEquals("2007-12-03 10:15:30", formatter.print(LocalDateTime.parse( "2007-12-03T10:15:30"), Locale.CHINA));
    }

    @Test
    void testOfPrinter() {
        Formatter<Object> formatter = DefaultFormatterFactory.ofPrinter((object, locale) -> "test");
        Assertions.assertEquals("test", formatter.print(DefaultExceptionCode.BAD_REQUEST, Locale.CHINA));
    }
}
