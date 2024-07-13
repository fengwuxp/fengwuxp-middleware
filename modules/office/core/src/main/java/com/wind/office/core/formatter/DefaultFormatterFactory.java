package com.wind.office.core.formatter;

import com.google.common.collect.ImmutableMap;
import com.wind.common.WindConstants;
import com.wind.common.WindDateFormatPatterns;
import com.wind.common.WindDateFormater;
import com.wind.common.enums.DescriptiveEnum;
import com.wind.common.exception.AssertUtils;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@link Formatter} 默认工厂
 *
 * @author wuxp
 * @date 2024-04-07 14:36
 **/
public final class DefaultFormatterFactory {

    private static final Map<String, DateTimeFormatter> DEFAULT_FORMATTERS = new HashMap<>();

    static {
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.ISO_8601_EXTENDED_DATETIME, WindDateFormater.ISO_8601_EXTENDED_DATETIME.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY_MM_DD_HH_MM_SS, WindDateFormater.YYYY_MM_DD_HH_MM_SS.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY_MM_DD_HH_MM, WindDateFormater.YYYY_MM_DD_HH_MM.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY_MM_DD_HH, WindDateFormater.YYYY_MM_DD_HH.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY_MM_DD, WindDateFormater.YYYY_MM_DD.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY_MM, WindDateFormater.YYYY_MM.getFormatter());
        DEFAULT_FORMATTERS.put(WindDateFormatPatterns.YYYY, WindDateFormater.YYYY.getFormatter());
    }

    private DefaultFormatterFactory() {
        throw new AssertionError();
    }

    public static Formatter<Boolean> ofBool(String trueDesc, String falseDesc) {
        return new MapFormatter<>(ImmutableMap.of(WindConstants.TRUE, trueDesc, Boolean.FALSE.toString(), falseDesc));
    }

    public static <T extends DescriptiveEnum> Formatter<T> ofEnum(Class<T> enumsClass) {
        AssertUtils.isTrue(enumsClass.isEnum(), "argument enumsClass must enum type");
        DescriptiveEnum[] enumConstants = enumsClass.getEnumConstants();
        HashMap<String, Object> source = new HashMap<>();
        for (DescriptiveEnum e : enumConstants) {
            source.put(e.name(), e.getDesc());
        }
        return new MapFormatter<>(source);
    }

    public static Formatter<TemporalAccessor> ofDateTime(String pattern) {
        DateTimeFormatter formatter = DEFAULT_FORMATTERS.containsKey(pattern) ? DEFAULT_FORMATTERS.get(pattern) : DateTimeFormatter.ofPattern(pattern);
        return new Formatter<TemporalAccessor>() {

            @Override
            public TemporalAccessor parse(String text, Locale locale) throws ParseException {
                return StringUtils.hasText(text) ? formatter.parse(text) : null;
            }

            @Override
            public String print(TemporalAccessor time, Locale locale) {
                return formatter.format(time);
            }
        };
    }
}
