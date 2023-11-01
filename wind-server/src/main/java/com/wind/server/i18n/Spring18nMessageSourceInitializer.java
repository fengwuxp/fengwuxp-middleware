package com.wind.server.i18n;

import com.wind.common.i18n.Spring18nMessageUtils;
import com.wind.web.utils.HttpServletRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * spring i18n 国际化支持 初始化器
 * {@link  org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.EnableWebMvcConfiguration#localeResolver()}
 *
 * @author wuxp
 * @date 2023-10-10 18:38
 * @see org.springframework.boot.autoconfigure.web.WebProperties.LocaleResolver#ACCEPT_HEADER
 * @see org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 * @see org.springframework.web.servlet.LocaleResolver
 * @see com.wind.server.configuration.I18nMessageSourceAutoConfiguration
 * @see Spring18nMessageUtils
 **/
@Slf4j
public class Spring18nMessageSourceInitializer implements ApplicationListener<ApplicationStartedEvent> {

    /**
     * {@link Locale} 解析器
     */
    private static final AtomicReference<LocaleResolver> LOCALE_RESOLVER = new AtomicReference<>();

    private static Locale getLocalWithRequest() {
        HttpServletRequest request = HttpServletRequestUtils.getContextRequestOfNullable();
        if (request == null) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        return LOCALE_RESOLVER.get().resolveLocale(request);
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationStartedEvent event) {
        try {
            LOCALE_RESOLVER.set(event.getApplicationContext().getBean(LocaleResolver.class));
            Spring18nMessageUtils.setMessageSource(event.getApplicationContext().getBean(MessageSource.class));
            Spring18nMessageUtils.setLocaleSupplier(Spring18nMessageSourceInitializer::getLocalWithRequest);
            log.info("enabled i18n supported");
        } catch (Exception ignore) {
            log.info("un enabled i18n supported");
        }
    }
}
