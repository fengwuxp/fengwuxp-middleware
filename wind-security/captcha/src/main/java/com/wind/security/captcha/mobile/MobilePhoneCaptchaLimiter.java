package com.wind.security.captcha.mobile;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.security.captcha.Captcha;
import com.wind.security.captcha.CaptchaGenerateLimiter;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Date;

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;

/**
 * @author wuxp
 * @date 2023-09-25 08:43
 **/
@AllArgsConstructor
public class MobilePhoneCaptchaLimiter implements CaptchaGenerateLimiter {

    /**
     * 图片验证码生成次数缓存缓存 key
     */
    private static final String CACHE_CAPTCHA_GENERATE_COUNT_STORE_KEY = "CAPTCHA_COUNT_CACHES";

    private final CacheManager cacheManager;

    /**
     * 业务模块分组
     */
    private final String group;

    /**
     * 每个用户每天允许发生验证码的最大次数
     */
    private final int mxAllowGenerateTimesOfUserWithDay;

    public MobilePhoneCaptchaLimiter(CacheManager cacheManager) {
        this(cacheManager, WindConstants.DEFAULT_TEXT.toUpperCase(), 15);
    }

    @Override
    public void checkAllowGenerate(String owner, Captcha.CaptchaType type) {
        String key = String.format("%s_%s", owner, ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()));
        Cache cache = getCache(type);
        Integer total = cache.get(key, Integer.class);
        if (total == null) {
            total = 0;
        } else {
            total += 1;
        }
        AssertUtils.isTrue(total < mxAllowGenerateTimesOfUserWithDay, "已超过每天允许发送的最大次数");
        cache.put(key, total);

    }

    private Cache getCache(Captcha.CaptchaType captchaTyp) {
        String name = String.format("%s_%s_%s", group, captchaTyp.name(), CACHE_CAPTCHA_GENERATE_COUNT_STORE_KEY);
        Cache result = cacheManager.getCache(name);
        AssertUtils.notNull(result, String.format("获取验证码生成次数 Cache 失败，CacheName = %s", name));
        return result;
    }
}
