package com.wind.security.captcha.picture;

import com.wind.security.captcha.Captcha;
import com.wind.security.captcha.CaptchaContentProvider;
import com.wind.security.captcha.CaptchaValue;
import com.wind.security.captcha.SimpleCaptchaType;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

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

    /**
     * 随机范围，移除数字 0、2 字母 i、o、z
     */
    private static final String RANDOM_STRINGS = "13456789ABCDEFGHJKMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy13456789";

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
        String value = randomCaptchaValue();
        String content = pictureGenerator.generateAndAsBas64(value, properties.getWidth(), properties.getHeight(), properties.getFormat());
        return CaptchaValue.of(value, content);
    }

    @Override
    public boolean supports(Captcha.CaptchaType type, Captcha.CaptchaUseScene useScene) {
        return Objects.equals(type, SimpleCaptchaType.PICTURE);
    }

    private String randomCaptchaValue() {
        int length = RANDOM_STRINGS.length();
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < properties.getLength(); ++i) {
            int index = RandomUtils.nextInt(0, length);
            content.append(RANDOM_STRINGS.charAt(index));
        }
        return content.toString();
    }

}
