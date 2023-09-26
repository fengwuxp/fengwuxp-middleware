package com.wind.security.captcha.picture;

import com.wind.security.captcha.Captcha;
import lombok.Data;

import java.time.Duration;

import static com.wind.security.captcha.picture.PictureGenerator.PNG_IMAGE_FORMAT;

/**
 * @author wuxp
 * @date 2023-09-24 12:42
 **/
@Data
public class PictureCaptchaProperties implements Captcha.CaptchaConfiguration {

    /**
     * 图片验证码长度
     */
    private int length = 4;

    /**
     * 验证码有效时长
     */
    private Duration effectiveTime = Duration.ofMinutes(3);

    /**
     * 最大可以验证次数
     */
    private int maxAllowVerificationTimes = 1;

    /**
     * 图片验证码宽度
     */
    private int width = 160;

    /**
     * 图片验证码高度
     */
    private int height = 50;

    /**
     * 图片输出格式
     */
    private String format = PNG_IMAGE_FORMAT;

}
