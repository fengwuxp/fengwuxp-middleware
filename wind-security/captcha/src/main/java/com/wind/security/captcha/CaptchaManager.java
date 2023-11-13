package com.wind.security.captcha;

/**
 * 验证码管理器
 *
 * @author wuxp
 * @date 2023-11-13 21:24
 **/
public interface CaptchaManager {


    /**
     * 生成验证码
     *
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param owner    验证码所有者
     * @return 验证码
     */
    Captcha generate(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner);

    /**
     * 验证验证码
     *
     * @param expected 预期的值
     * @param type     验证码类型
     * @param useScene 验证码使用场景
     * @param owner    验证码所有者
     */
    void verify(String expected, Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene, String owner);
}

