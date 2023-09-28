package com.wind.security.captcha.configuration;

import com.wind.common.WindConstants;
import com.wind.security.captcha.Captcha;
import com.wind.security.captcha.SimpleCaptchaType;
import com.wind.security.captcha.email.EmailCaptchaProperties;
import com.wind.security.captcha.mobile.MobilePhoneCaptchaProperties;
import com.wind.security.captcha.picture.PictureCaptchaProperties;
import com.wind.security.captcha.qrcode.QrCodeCaptchaProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @author wuxp
 * @date 2023-09-24 15:33
 **/
@Data
@ConfigurationProperties(prefix = CaptchaProperties.PREFIX)
public class CaptchaProperties {

    /**
     * 配置 prefix
     */
    public static final String PREFIX = "wind.security.captcha";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 验证时忽略大小写
     */
    private boolean verificationIgnoreCase;

    /**
     * 业务分组
     */
    private String group = WindConstants.DEFAULT_TEXT.toUpperCase();

    /**
     * 手机短信验证码
     */
    private MobilePhoneCaptchaProperties mobilePhone = new MobilePhoneCaptchaProperties();

    /**
     * 邮箱验证码
     */
    private EmailCaptchaProperties email = new EmailCaptchaProperties();

    /**
     * 图片验证码
     */
    private PictureCaptchaProperties picture = new PictureCaptchaProperties();

    /**
     * 二维码
     */
    private QrCodeCaptchaProperties qrCode = new QrCodeCaptchaProperties();

    public int getMxAllowGenerateTimesOfUserWithDay(Captcha.CaptchaType type) {
        if (Objects.equals(SimpleCaptchaType.MOBILE_PHONE, type)) {
            return mobilePhone.getMxAllowGenerateTimesOfUserWithDay();
        }

        if (Objects.equals(SimpleCaptchaType.EMAIL, type)) {
            return email.getMxAllowGenerateTimesOfUserWithDay();
        }
        return Integer.MAX_VALUE;
    }
}
