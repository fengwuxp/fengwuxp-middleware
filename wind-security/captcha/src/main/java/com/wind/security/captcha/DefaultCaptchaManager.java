package com.wind.security.captcha;

import com.wind.common.annotations.VisibleForTesting;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Collection;
import java.util.Date;

/**
 * @author wuxp
 * @date 2023-09-24 10:13
 **/
@AllArgsConstructor
public class DefaultCaptchaManager {

    private final Collection<CaptchaContentProvider> delegates;

    @Getter
    @VisibleForTesting
    private final CaptchaStorage captchaStorage;

    /**
     * 验证时忽略大小写
     */
    private final boolean verificationIgnoreCase;

    public DefaultCaptchaManager(Collection<CaptchaContentProvider> delegates, CaptchaStorage captchaStorage) {
        this(delegates, captchaStorage, true);
    }

    public Captcha generate(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner) {
        CaptchaContentProvider delegate = getDelegate(type, useScene);
        CaptchaValue captchaValue = delegate.getValue(owner, useScene);
        ImmutableCaptcha result = ImmutableCaptcha.builder()
                .content(captchaValue.getContent())
                .value(captchaValue.getValue())
                .owner(owner)
                .type(type)
                .useScene(useScene)
                .expireTime(DateUtils.addSeconds(new Date(), (int) delegate.getEffectiveTime().getSeconds()))
                .verificationCount(0)
                .allowVerificationTimes(delegate.getMaxAllowVerificationTimes())
                .build();
        captchaStorage.store(result);
        return result;
    }

    /**
     * 验证验证码
     *
     * @param expected 预期的值
     * @param useScene 验证码使用场景
     * @param owner    验证码所有者
     */
    public void verify(String expected, Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner) {
        Captcha captcha = captchaStorage.get(type, useScene, owner);
        AssertUtils.notNull(captcha, "验证码不存在");
        if (!captcha.isEffective()) {
            // 验证码已失效，移除
            captchaStorage.remove(type, useScene, owner);
            throw BaseException.common("验证码已失效");
        }
        boolean isPass = verificationIgnoreCase ? captcha.getValue().equalsIgnoreCase(expected) : captcha.getValue().equals(expected);
        if (isPass) {
            // 移除
            captchaStorage.remove(type, useScene, owner);
        } else {
            Captcha next = captcha.increase();
            if (next.isEffective()) {
                // 还可以继续用于验证，更新验证码
                captchaStorage.store(next);
            } else {
                // 验证码已失效，移除
                captchaStorage.remove(type, useScene, owner);
            }
            throw BaseException.common("验证码验证失败");
        }
    }

    private CaptchaContentProvider getDelegate(Captcha.CaptchaType type, Captcha.CaptchaUseScene scene) {
        for (CaptchaContentProvider delegate : delegates) {
            if (delegate.supports(type, scene)) {
                return delegate;
            }
        }
        throw BaseException.notFound(String.format("未找到：type = %s，scene = %s 的验证码提供者", type.getDesc(), scene.getDesc()));
    }

}
