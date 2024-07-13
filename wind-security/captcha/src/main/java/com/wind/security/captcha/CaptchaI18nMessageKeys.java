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
     * 验证码不存在或已失效
     */
    private static final String CAPTCHA_NOT_EXIST_OR_EXPIRED = "$.%s.captcha.not.exist.or.expired";

    /**
     * 验证码发送流控
     */
    public static final String CAPTCHA_FLOW_CONTROL = "$.captcha.flow.control";

    /**
     * 一个用户每天允许生成的验证码的最大次数
     */
    public static final String CAPTCHA_GENERATE_MAX_LIMIT_OF_USER_BY_DAY = "$.captcha.generate.limit.user-by-day";

    /**
     * 并发生成验证码
     */
    public static final String CAPTCHA_CONCURRENT_GENERATE = "$.captcha.concurrent.generate.error";

    /**
     * 验证码验证失败
     */
    private static final String CAPTCHA_VERITY_FAILURE = "$.%s.captcha.verify.failure";


    public static String getCaptchaNotExistOrExpired(Captcha.CaptchaType type) {
        return String.format(CAPTCHA_NOT_EXIST_OR_EXPIRED, type.name());
    }

    public static String getCaptchaVerityFailure(Captcha.CaptchaType type) {
        return String.format(CAPTCHA_VERITY_FAILURE, type.name());
    }
}
