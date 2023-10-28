package com.wind.office.core.formatter;

import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

/**
 * 根据 map 进行值转换
 *
 * @author wuxp
 */
@Slf4j
public class MapFormatter implements Formatter<String> {

    private final Map<String, String> dataSource;

    public MapFormatter(Map<String, String> map) {
        this.dataSource = map;
    }

    @Nonnull
    @Override
    public String parse(@Nullable String text, @Nullable Locale locale) throws ParseException {
        if (text != null) {
            for (Map.Entry<String, String> entry : this.dataSource.entrySet()) {
                if (entry.getValue().equals(text)) {
                    return entry.getKey();
                }
            }
        }
        return WindConstants.EMPTY;
    }

    @Nonnull
    @Override
    public String print(@Nullable String text, @Nullable Locale locale) {
        if (text == null) {
            return "";
        }
        return this.dataSource.get(text);
    }
}
