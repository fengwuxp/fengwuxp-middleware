package com.wind.office.core.formatter;

import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * 分转元
 *
 * @author wuxp
 */
@Slf4j
public class FenToYanFormatter implements Formatter<Number> {

    private final BigDecimal decimal = new BigDecimal(100);

    @Nullable
    @Override
    public Number parse(@Nullable String text, @Nullable Locale locale) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return BigDecimal.valueOf(Long.parseLong(text))
                .divide(decimal, RoundingMode.CEILING)
                .setScale(2, RoundingMode.UP);
    }

    @Nonnull
    @Override
    public String print(@Nullable Number object, @Nullable Locale locale) {
        if (object == null) {
            return WindConstants.EMPTY;
        }
        String s = BigDecimal.valueOf(object.longValue())
                .divide(decimal)
                .setScale(5, RoundingMode.UP)
                .toString();
        return s.substring(0, s.indexOf(".") + 3);
    }

}
