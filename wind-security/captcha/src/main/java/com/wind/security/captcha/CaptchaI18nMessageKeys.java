package com.wind.security.captcha;

/**
 * 验证码相关消息 i18n key
 *
 * @author wuxp
 * @date 2023-10-11 09:42
 **/
public final class CaptchaI18nMessageKeys {

    private CaptchaI18nMessageKeys() {
        throw new AssertionError();
    }

    /**
     * 验证码不存在
     */
    public static final String CAPTCHA_NOT_EXIST = "$.captcha.not.exist";

    /**
     * 验证码已失效
     */
    public static final String CAPTCHA_EXPIRED = "$.captcha.expired";

    /**
     * 验证码验证失败
     */
    public static final String CAPTCHA_VERITY_FAILURE = "$.captcha.verify.failure";
}
