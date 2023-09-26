package com.wind.security.authentication;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证方式
 *
 * @author wuxp
 * @date 2023-09-23 13:48
 **/
@AllArgsConstructor
@Getter
public enum AuthenticationMethod implements DescriptiveEnum {


    /**
     * 密码认证
     */
    PASSWORD("密码登录"),

    /**
     * 手机验证码认证
     */
    MOBILE_CAPTCHA("手机验证码登录"),

    /**
     * 扫码认证
     */
    SCAN_CODE("扫码登录");


    private final String desc;

}
