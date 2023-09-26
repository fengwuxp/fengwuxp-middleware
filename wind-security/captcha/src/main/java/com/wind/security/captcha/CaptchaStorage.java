package com.wind.security.captcha;

import org.springframework.lang.Nullable;

/**
 * 验证码存储器
 *
 * @author wuxp
 * @date 2023-09-24 10:06
 **/
public interface CaptchaStorage {

    /**
     * 保存验证码
     *
     * @param captcha 验证码
     */
    void store(Captcha captcha);

    /**
     * 获取验证码
     *
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param key      验证码存储 key
     */
    @Nullable
    Captcha get(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String key);

    /**
     * 删除验证码
     *
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param key      验证码存储 key
     */
    void remove(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String key);

    /**
     * 移除某个类型的验证码
     *
     * @param type 验证码类型
     */
    default void removeAll(Captcha.CaptchaType type) {
    }
}
