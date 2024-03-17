package com.wind.security.captcha.storage;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.security.captcha.Captcha;
import com.wind.security.captcha.CaptchaConstants;
import com.wind.security.captcha.CaptchaStorage;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wuxp
 * @date 2023-09-24 14:43
 **/
@AllArgsConstructor
public class CacheCaptchaStorage implements CaptchaStorage {


    private final CacheManager cacheManager;

    /**
     * 业务模块分组
     */
    private final String group;

    public CacheCaptchaStorage(CacheManager cacheManager) {
        this(cacheManager, WindConstants.DEFAULT_TEXT.toUpperCase());
    }

    @Override
    public void store(Captcha captcha) {
        Cache cache = requiredCache(captcha.getType(), captcha.getUseScene());
        if (captcha.getType().isSupportMultiple()) {
            Set<Captcha> captchaes = getCaptchaes(cache, captcha.getOwner());
            // 过滤掉已存在的验证码
            Set<Captcha> newCaptchaes = captchaes.stream().filter(c -> !Objects.equals(c.getValue(), captcha.getValue())).collect(Collectors.toSet());
            newCaptchaes.add(captcha);
            // 重新保存
            cache.put(captcha.getOwner(), newCaptchaes);
        } else {
            cache.put(captcha.getOwner(), Collections.singleton(captcha));
        }
    }

    @Override
    public Collection<Captcha> get(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String key) {
        Cache cache = requiredCache(type, useScene);
        Set<Captcha> result = getCaptchaes(cache, key);
        if (ObjectUtils.isEmpty(result)) {
            cache.evict(key);
        } else {
            cache.put(key, result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Set<Captcha> getCaptchaes(Cache cache, String key) {
        Set<Captcha> captchaes = cache.get(key, Set.class);
        if (captchaes == null) {
            return new HashSet<>(6);
        }
        // 过滤出有效的验证码
        return captchaes.stream().filter(Captcha::isEffective).collect(Collectors.toSet());
    }

    @Override
    public void remove(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String key) {
        requiredCache(type, useScene).evict(key);
    }

    @NonNull
    private Cache requiredCache(Captcha.CaptchaType captchaTyp, Captcha.CaptchaUseScene useScene) {
        String name = CaptchaConstants.getCaptchaCacheName(group, captchaTyp, useScene);
        Cache result = cacheManager.getCache(name);
        AssertUtils.notNull(result, String.format("获取验证码 Cache 失败，CacheName = %s", name));
        return result;
    }
}
