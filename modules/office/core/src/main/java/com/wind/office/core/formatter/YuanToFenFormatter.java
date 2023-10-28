package com.wind.office.core.formatter;

import com.wind.common.WindConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

/**
 * 元转分
 *
 * @author wuxp
 */
@Slf4j
public class YuanToFenFormatter implements Formatter<Number> {

    private final BigDecimal decimal = new BigDecimal(100);

    @Override
    public Number parse(@Nullable String text, @Nullable Locale locale) throws ParseException {
        if (StringUtils.hasText(text)) {
            return new BigDecimal(text).multiply(decimal).longValue();
        }
        return null;
    }

    @Nonnull
    @Override
    public String print(@Nullable Number object, @Nullable Locale locale) {
        if (object == null) {
            return WindConstants.EMPTY;
        }
        return new BigDecimal(object.toString()).multiply(decimal).toBigInteger().toString();
    }
}
