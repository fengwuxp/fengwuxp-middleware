package com.wind.security.captcha;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2023-09-24 09:35
 **/
@AllArgsConstructor
@Getter
public enum SimpleUseScene implements Captcha.CaptchaUseScene {

    LOGIN("登录"),

    REGISTER("注册"),

    REST_PASSWORD("重置密码"),

    PAY("支付"),

    BIND_MOBILE_PHONE("绑定手机"),

    /**
     * 例如：实名认证等场景
     */
    IDENTITY_CONFIRMATION("身份确认");

    private final String desc;
}
