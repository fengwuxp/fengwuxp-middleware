package com.wind.common.i18n;

import com.wind.common.WindConstants;
import com.wind.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * spring i18n 国际化支持
 * 非常规用法，主要为了方便使用
 *
 * @author wuxp
 * @date 2023-11-01 15:05
 **/
@Slf4j
public final class Spring18nMessageUtils {

    private Spring18nMessageUtils() {
        throw new AssertionError();
    }

    static {
        BaseException.setI18nMessageFormatter(Spring18nMessageUtils::getMessage);
    }

    /**
     * {@link Locale} Supplier
     */
    private static final AtomicReference<Supplier<Locale>> LOCALE_SUPPLIER = new AtomicReference<>();

    /**
     * i18n 消息源
     */
    private static final AtomicReference<MessageSource> MESSAGE_SOURCE = new AtomicReference<>();

    /**
     * i18n 消息 key 匹配器
     */
    private static final AtomicReference<Predicate<String>> I18N_KEY_MATCHER = new AtomicReference<>(text -> text.startsWith("$."));

    public static String getMessage(String message) {
        return getMessage(message, WindConstants.EMPTY);
    }

    public static String getMessage(String message, String defaultMessage) {
        return getMessage(message, defaultMessage, null);
    }

    public static String getMessage(String message, String defaultMessage, Locale locale) {
        return getMessage(message, null, defaultMessage, locale);
    }

    public static String getMessage(String message, @Nullable Object[] args) {
        return getMessage(message, args, null, null);
    }

    public static String getMessage(String message, @Nullable Object[] args, Locale locale) {
        return getMessage(message, args, null, locale);
    }

    public static String getMessage(String message, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        if (message == null) {
            return "null";
        }
        if (MESSAGE_SOURCE.get() == null || !I18N_KEY_MATCHER.get().test(message)) {
            return defaultMessage == null ? message : defaultMessage;
        }
        String result = MESSAGE_SOURCE.get().getMessage(message, args, defaultMessage, getLocale(locale));
        // 未获取到消息返回默认消息或原本消息
        if (result == null) {
            return defaultMessage == null ? message : defaultMessage;
        }
        return result;
    }

    private static Locale getLocale(Locale locale) {
        if (locale != null || LOCALE_SUPPLIER.get() == null) {
            return locale;
        }
        Locale result = LOCALE_SUPPLIER.get().get();
        return result == null ? Locale.CHINA : result;
    }

    public static void setMessageSource(MessageSource messageSource) {
        MESSAGE_SOURCE.set(messageSource);
    }

    public static void setLocaleSupplier(Supplier<Locale> supplier) {
        LOCALE_SUPPLIER.set(supplier);
    }

    /**
     * @return 获取当前上下文的 locale
     */
    @NotNull
    public static Locale requireLocale() {
        return getLocale(Locale.CHINA);
    }

    /**
     * 设置 i18n 消息 key 的匹配器
     *
     * @param matcher 匹配器
     */
    public static void setI18nKeyMatcher(Predicate<String> matcher) {
        I18N_KEY_MATCHER.set(matcher);
    }
}
