package com.wind.server.i18n;

import com.wind.common.WindConstants;
import com.wind.common.exception.BaseException;
import com.wind.server.utils.HttpServletRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * spring i18n 国际化支持
 * 非常规用法，主要为了方便使用
 * {@link  org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.EnableWebMvcConfiguration#localeResolver()}
 *
 * @author wuxp
 * @date 2023-10-10 18:38
 * @see org.springframework.boot.autoconfigure.web.WebProperties.LocaleResolver#ACCEPT_HEADER
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @see org.springframework.web.servlet.LocaleResolver
 **/
@Slf4j
public class Spring18nMessageUtils implements ApplicationListener<ApplicationStartedEvent> {

    /**
     * {@link Locale} 解析器
     */
    private static final AtomicReference<LocaleResolver> LOCALE_RESOLVER = new AtomicReference<>();

    /**
     * i18n 消息源
     */
    private static final AtomicReference<MessageSource> MESSAGE_SOURCE = new AtomicReference<>();

    /**
     * 判断字符串是否为 i18n 消息 key 的匹配器
     */
    private static final String DEFAULT_I18N_KEY_PREFIX = "$.";

    /**
     * i18n 消息 key 匹配器
     */
    private static final AtomicReference<Predicate<String>> I18N_KEY_MATCHER = new AtomicReference<>(text -> text.startsWith(DEFAULT_I18N_KEY_PREFIX));

    public static String getMessage(String message) {
        return getMessage(message, WindConstants.EMPTY);
    }

    public static String getMessage(String message, String defaultMessage) {
        return getMessage(message, defaultMessage, null);
    }

    public static String getMessage(String message, String defaultMessage, Locale locale) {
        return getMessage(message, null, defaultMessage, locale);
    }

    public static String getMessage(String code, @Nullable Object[] args, Locale locale) {
        return getMessage(code, args, null, locale);
    }

    public static String getMessage(String message, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        if (MESSAGE_SOURCE.get() != null && StringUtils.hasLength(message) && I18N_KEY_MATCHER.get().test(message)) {
            return MESSAGE_SOURCE.get().getMessage(message, args, defaultMessage, getLocalWithRequest(locale));
        }
        return message;
    }

    private static Locale getLocalWithRequest(Locale locale) {
        HttpServletRequest request = HttpServletRequestUtils.getContextRequestOfNullable();
        if (request == null) {
            return locale == null ? Locale.SIMPLIFIED_CHINESE : locale;
        }
        return LOCALE_RESOLVER.get().resolveLocale(request);
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationStartedEvent event) {
        try {
            LOCALE_RESOLVER.set(event.getApplicationContext().getBean(LocaleResolver.class));
            MESSAGE_SOURCE.set(event.getApplicationContext().getBean(MessageSource.class));
            BaseException.setMessageI18n(Spring18nMessageUtils::getMessage);
            log.info("enabled i18n supported");
        } catch (Exception ignore) {
            log.info("un enabled i18n supported");
        }
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
