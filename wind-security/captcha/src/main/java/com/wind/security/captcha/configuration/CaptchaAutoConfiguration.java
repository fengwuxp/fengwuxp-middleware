package com.wind.security.captcha.configuration;

import com.wind.security.captcha.CaptchaContentProvider;
import com.wind.security.captcha.CaptchaGenerateLimiter;
import com.wind.security.captcha.CaptchaStorage;
import com.wind.security.captcha.DefaultCaptchaManager;
import com.wind.security.captcha.mobile.MobilePhoneCaptchaContentProvider;
import com.wind.security.captcha.mobile.MobilePhoneCaptchaLimiter;
import com.wind.security.captcha.picture.PictureCaptchaContentProvider;
import com.wind.security.captcha.picture.PictureGenerator;
import com.wind.security.captcha.storage.CacheCaptchaStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author wuxp
 * @date 2023-09-24 15:33
 **/
@Configuration
@EnableConfigurationProperties(value = {CaptchaProperties.class})
@ConditionalOnBean({CacheManager.class})
@ConditionalOnProperty(prefix = CaptchaProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class CaptchaAutoConfiguration {


    @ConditionalOnBean(CaptchaStorage.class)
    @Bean
    public DefaultCaptchaManager defaultCaptchaManager(Collection<CaptchaContentProvider> delegates, CaptchaStorage captchaStorage,
                                                       CaptchaProperties properties) {
        return new DefaultCaptchaManager(delegates, captchaStorage, properties.isVerificationIgnoreCase());
    }

    @Bean
    @ConditionalOnBean({PictureGenerator.class})
    public PictureCaptchaContentProvider pictureCaptchaContentProvider(CaptchaProperties properties, PictureGenerator pictureGenerator) {
        return new PictureCaptchaContentProvider(properties.getPicture(), pictureGenerator);
    }

    @Bean
    @ConditionalOnBean({ CaptchaGenerateLimiter.class})
    public MobilePhoneCaptchaContentProvider mobilePhoneCaptchaContentProvider(CaptchaProperties properties, CaptchaGenerateLimiter limiter) {
        return new MobilePhoneCaptchaContentProvider(properties.getMobilePhone(), limiter);
    }

    @ConditionalOnBean(CacheManager.class)
    @ConditionalOnMissingBean(CacheCaptchaStorage.class)
    @Bean
    public CacheCaptchaStorage cacheCaptchaStorage(CacheManager cacheManager, CaptchaProperties properties) {
        return new CacheCaptchaStorage(cacheManager, properties.getGroup());
    }

    @ConditionalOnBean(CacheManager.class)
    @ConditionalOnMissingBean(MobilePhoneCaptchaLimiter.class)
    @Bean
    public MobilePhoneCaptchaLimiter mobilePhoneCaptchaLimiter(CacheManager cacheManager, CaptchaProperties properties) {
        return new MobilePhoneCaptchaLimiter(cacheManager, properties.getGroup(), properties.getMobilePhone().getMxAllowGenerateTimesOfUserWithDay());
    }
}
