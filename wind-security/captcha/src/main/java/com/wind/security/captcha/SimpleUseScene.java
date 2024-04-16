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

    CHANGE_PASSWORD("修改密码"),

    REST_PASSWORD("重置密码"),

    BIND_MOBILE_PHONE("绑定手机"),

    BIND_EMAIL("绑定邮箱"),

    /**
     * 例如：实名认证等场景
     */
    IDENTITY_CONFIRMATION("身份确认"),

    PAYMENT("支付");

    private final String desc;
}
