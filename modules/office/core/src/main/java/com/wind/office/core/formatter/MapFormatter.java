package com.wind.office.core.formatter;

import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;

/**
 * 根据 map 进行值转换
 *
 * @author wuxp
 */
@Slf4j
public class MapFormatter<T> implements Formatter<T> {

    private final Map<String, Object> dataSource;

    public MapFormatter(Map<String, Object> source) {
        this.dataSource = source;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public T parse(@Nullable String text, @Nullable Locale locale) {
        if (text != null) {
            for (Map.Entry<String, Object> entry : this.dataSource.entrySet()) {
                if (entry.getValue().equals(text)) {
                    return (T) entry.getKey();
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String print(@Nullable T value, @Nullable Locale locale) {
        if (value == null) {
            return WindConstants.EMPTY;
        }
        return (String) this.dataSource.get(String.valueOf(value));
    }
}
