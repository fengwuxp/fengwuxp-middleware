package com.wind.security.captcha.qrcode;

import com.wind.security.captcha.Captcha;
import lombok.Data;

import java.time.Duration;

/**
 * @author wuxp
 * @date 2023-09-24 14:04
 **/
@Data
public class QrCodeCaptchaProperties implements Captcha.CaptchaConfiguration {

    /**
     * 验证码有效时长
     */
    private Duration effectiveTime = Duration.ofMinutes(3);

    /**
     * 最大可以验证次数
     */
    private int maxAllowVerificationTimes = 15;

    /**
     * 二维码配置
     */
    private QrCodeGenerator.QrCodeConfig qrCode = new QrCodeGenerator.QrCodeConfig();
}
