package com.wind.security.captcha;

import com.wind.common.annotations.VisibleForTesting;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

/**
 * @author wuxp
 * @date 2023-09-24 10:13
 **/
@AllArgsConstructor
public class DefaultCaptchaManager implements CaptchaManager {

    private final Collection<CaptchaContentProvider> delegates;

    @Getter
    @VisibleForTesting
    private final CaptchaStorage captchaStorage;

    private final CaptchaGenerateChecker generateChecker;

    /**
     * 验证时忽略大小写
     */
    private final boolean verificationIgnoreCase;

    public DefaultCaptchaManager(Collection<CaptchaContentProvider> delegates, CaptchaStorage captchaStorage, boolean verificationIgnoreCase) {
        this(delegates, captchaStorage, (owner, type) -> {
        }, verificationIgnoreCase);
    }

    public DefaultCaptchaManager(Collection<CaptchaContentProvider> delegates, CaptchaStorage captchaStorage, CaptchaGenerateChecker generateChecker) {
        this(delegates, captchaStorage, generateChecker, true);
    }

    /**
     * 生成验证码
     *
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param owner    验证码所有者
     * @return 验证码
     */
    @Override
    public Captcha generate(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner) {
        // 检查是否允许生成验证码
        generateChecker.preCheck(owner, type);
        CaptchaContentProvider delegate = getDelegate(type, useScene);
        CaptchaValue captchaValue = delegate.getValue(owner, useScene);
        Captcha result = ImmutableCaptcha.builder()
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
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param owner    验证码所有者
     */
    @Override
    public void verify(String expected, Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner) {
        Collection<Captcha> captchaes = captchaStorage.get(type, useScene, owner);
        AssertUtils.notEmpty(captchaes, CaptchaI18nMessageKeys.CAPTCHA_NOT_EXIST_OR_EXPIRED);
        int invalidCount = 0;
        for (Captcha captcha : captchaes) {
            boolean isPass = verificationIgnoreCase ? captcha.getValue().equalsIgnoreCase(expected) : captcha.getValue().equals(expected);
            if (isPass) {
                // 验证通过，移除
                captchaStorage.remove(type, useScene, owner);
                return;
            } else {
                Captcha next = captcha.increase();
                if (next.isEffective()) {
                    // 还可以继续用于验证，更新验证码
                    captchaStorage.store(next);
                } else {
                    // 累计已失效的验证码个数
                    invalidCount++;
                }
            }
        }
        if (Objects.equals(invalidCount, captchaes.size())) {
            // 所有验证码都已经失效，移除
            captchaStorage.remove(type, useScene, owner);
        }
        throw BaseException.common(CaptchaI18nMessageKeys.CAPTCHA_VERITY_FAILURE);
    }

    private CaptchaContentProvider getDelegate(Captcha.CaptchaType type, Captcha.CaptchaUseScene scene) {
        for (CaptchaContentProvider delegate : delegates) {
            if (delegate.supports(type, scene)) {
                return delegate;
            }
        }
        throw BaseException.notFound(String.format("un found：type = %s，scene = %s CaptchaContentProvider", type.getDesc(), scene.getDesc()));
    }

}
