package com.wind.server.i18n;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @author wuxp
 * @date 2024-07-16 11:20
 * @see org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
 **/
public class AcceptI18nHeaderLocaleResolver implements LocaleResolver {

    private final List<Locale> supportedLocales = new ArrayList<>(4);

    @Nullable
    private Locale defaultLocale;

    private final List<String> headerNames;

    public AcceptI18nHeaderLocaleResolver(List<String> headerNames) {
        this.headerNames = headerNames;
    }

    public AcceptI18nHeaderLocaleResolver() {
        this(Collections.singletonList("Accept-Language"));
    }

    /**
     * Configure supported locales to check against the requested locales
     * determined via {@link HttpServletRequest#getLocales()}. If this is not
     * configured then {@link HttpServletRequest#getLocale()} is used instead.
     *
     * @param locales the supported locales
     * @since 4.3
     */
    public void setSupportedLocales(List<Locale> locales) {
        this.supportedLocales.clear();
        this.supportedLocales.addAll(locales);
    }

    /**
     * Get the configured list of supported locales.
     *
     * @since 4.3
     */
    public List<Locale> getSupportedLocales() {
        return this.supportedLocales;
    }

    /**
     * Configure a fixed default locale to fall back on if the request does not
     * have an "Accept-Language" header.
     * <p>By default this is not set in which case when there is no "Accept-Language"
     * header, the default locale for the server is used as defined in
     * {@link HttpServletRequest#getLocale()}.
     *
     * @param defaultLocale the default locale to use
     * @since 4.3
     */
    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * The configured default locale, if any.
     * <p>This method may be overridden in subclasses.
     *
     * @since 4.3
     */
    @Nullable
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }


    @Override
    public Locale resolveLocale(@NotNull HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale != null && getLanguageHeader(request) == null) {
            return defaultLocale;
        }
        Locale requestLocale = request.getLocale();
        List<Locale> supportedLocales = getSupportedLocales();
        if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
            return requestLocale;
        }
        Locale supportedLocale = findSupportedLocale(request, supportedLocales);
        if (supportedLocale != null) {
            return supportedLocale;
        }
        return (defaultLocale != null ? defaultLocale : requestLocale);
    }

    @Nullable
    private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
        Enumeration<Locale> requestLocales = request.getLocales();
        Locale languageMatch = null;
        while (requestLocales.hasMoreElements()) {
            Locale locale = requestLocales.nextElement();
            if (supportedLocales.contains(locale)) {
                if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
                    // Full match: language + country, possibly narrowed from earlier language-only match
                    return locale;
                }
            } else if (languageMatch == null) {
                // Let's try to find a language-only match as a fallback
                for (Locale candidate : supportedLocales) {
                    if (!StringUtils.hasLength(candidate.getCountry()) &&
                            candidate.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = candidate;
                        break;
                    }
                }
            }
        }
        return languageMatch;
    }

    @Override
    public void setLocale(@NotNull HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale) {
        throw new UnsupportedOperationException(
                "Cannot change HTTP Accept-Language header - use a different locale resolution strategy");
    }

    private String getLanguageHeader(HttpServletRequest request) {
        return headerNames.stream()
                .map(request::getHeader)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

}
