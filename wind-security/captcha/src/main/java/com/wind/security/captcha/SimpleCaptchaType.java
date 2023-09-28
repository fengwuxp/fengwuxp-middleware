package com.wind.security.captcha;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2023-09-24 09:24
 **/
@AllArgsConstructor
@Getter
public enum SimpleCaptchaType implements Captcha.CaptchaType {


    MOBILE_PHONE("短信验证码"),

    EMAIL("邮箱验证码"),

    PICTURE("图片验证码"),


    QR_CODE("二维码验证码");

    private final String desc;
}
