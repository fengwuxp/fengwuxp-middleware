package com.wind.security.captcha.picture;

import com.wind.security.captcha.Captcha;
import com.wind.security.captcha.CaptchaContentProvider;
import com.wind.security.captcha.CaptchaValue;
import com.wind.security.captcha.SimpleCaptchaType;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * 图片验证码提供者
 *
 * @author wuxp
 * @date 2023-09-24 10:33
 **/
@AllArgsConstructor
public class PictureCaptchaContentProvider implements CaptchaContentProvider {


    private final PictureCaptchaProperties properties;

    private final PictureGenerator pictureGenerator;

    @Override
    public Duration getEffectiveTime() {
        return properties.getEffectiveTime();
    }

    @Override
    public int getMaxAllowVerificationTimes() {
        return properties.getMaxAllowVerificationTimes();
    }

    @Override
    public CaptchaValue getValue(String owner, Captcha.CaptchaUseScene useScene) {
        String value = RandomStringUtils.randomAlphanumeric(properties.getLength());
        String content = pictureGenerator.generateAndAsBas64(value, properties.getWidth(), properties.getHeight(), properties.getFormat());
        return CaptchaValue.of(value, content);
    }


    @Override
    public boolean supports(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene) {
        return Objects.equals(type, SimpleCaptchaType.PICTURE);
    }


}
