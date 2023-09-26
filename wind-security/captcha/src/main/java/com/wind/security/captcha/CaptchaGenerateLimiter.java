package com.wind.security.captcha;

/**
 * 验证码生成限制器，避免被暴力攻击
 *
 * @author wuxp
 * @date 2023-09-25 08:27
 **/
@FunctionalInterface
public interface CaptchaGenerateLimiter {

    /**
     * 检查是否允许生成验证码，如果不允许则抛出异常
     *
     * @param owner 验证码所有者
     * @param type  验证码类型
     */
    void checkAllowGenerate(String owner, Captcha.CaptchaType type);
}
