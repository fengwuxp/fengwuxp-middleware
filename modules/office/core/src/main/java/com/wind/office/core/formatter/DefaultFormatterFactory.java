package com.wind.office.core.formatter;

import com.google.common.collect.ImmutableMap;
import com.wind.common.WindConstants;
import com.wind.common.enums.DescriptiveEnum;
import com.wind.common.exception.AssertUtils;
import org.springframework.format.Formatter;
import org.springframework.format.Printer;

import java.util.HashMap;
import java.util.Locale;

/**
 * {@link Formatter} 默认工厂
 *
 * @author wuxp
 * @date 2024-04-07 14:36
 **/
public final class DefaultFormatterFactory {

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

    public static <T> Formatter<T> ofPrinter(Printer<T> printer) {
        return new Formatter<T>() {
            @Override
            public T parse(String text, Locale locale) {
                return null;
            }

            @Override
            public String print(T object, Locale locale) {
                return printer.print(object, locale);
            }
        };
    }
}
