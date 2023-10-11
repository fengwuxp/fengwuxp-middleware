package com.wind.security.captcha;

import com.wind.common.exception.BaseException;
import com.wind.security.captcha.mobile.MobilePhoneCaptchaContentProvider;
import com.wind.security.captcha.mobile.MobilePhoneCaptchaProperties;
import com.wind.security.captcha.picture.PictureCaptchaContentProvider;
import com.wind.security.captcha.picture.PictureCaptchaProperties;
import com.wind.security.captcha.picture.SimplePictureGenerator;
import com.wind.security.captcha.qrcode.QrCodeCaptchaContentProvider;
import com.wind.security.captcha.qrcode.QrCodeCaptchaProperties;
import com.wind.security.captcha.storage.CacheCaptchaStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Arrays;
import java.util.Collection;

class DefaultCaptchaManagerTest {

    private DefaultCaptchaManager captchaManager;

    private final static int TEST_MX_ALLOW_GENERATE_TIMES_OF_USER_WITH_DAY = 15;

    @BeforeEach
    void setup() {
        CaptchaGenerateChecker checker = new SimpleCaptchaGenerateChecker(new ConcurrentMapCacheManager(), "test", type -> TEST_MX_ALLOW_GENERATE_TIMES_OF_USER_WITH_DAY);
        captchaManager = new DefaultCaptchaManager(getProviders(), getCaptchaStorage(), checker);
    }

    @Test
    void testPictureCaptchaWhitPaas() {
        assertCaptchaPaas(SimpleCaptchaType.PICTURE);
    }

    @Test
    void testMobileCaptchaWhitPaas() {
        assertCaptchaPaas(SimpleCaptchaType.MOBILE_PHONE);
    }

    @Test
    void tesQrCodeCaptchaWhitPaas() {
        assertCaptchaPaas(SimpleCaptchaType.QR_CODE);
    }

    private void assertCaptchaPaas(Captcha.CaptchaType type) {
        for (Captcha.CaptchaUseScene scene : SimpleUseScene.values()) {
            String owner = RandomStringUtils.randomAlphanumeric(12);
            Captcha captcha = captchaManager.generate(type, scene, owner);
            Assertions.assertNotNull(captcha);
            captchaManager.verify(captcha.getValue(), type, scene, captcha.getOwner());
            Captcha next = captchaManager.getCaptchaStorage().get(captcha.getType(), captcha.getUseScene(), captcha.getOwner());
            Assertions.assertNull(next);
        }
    }

    @Test
    void testPictureCaptchaWithError() {
        assertCaptchaError(SimpleCaptchaType.PICTURE, 1);
    }

    @Test
    void testMobileCaptchaWithError() {
        assertCaptchaError(SimpleCaptchaType.MOBILE_PHONE, 5);
    }

    @Test
    void testQrCodeCaptchaWithError() {
        assertCaptchaError(SimpleCaptchaType.QR_CODE, 15);
    }

    @Test
    void testMobileCaptchaGenerateLimit() {
        String owner = RandomStringUtils.randomAlphanumeric(11);
        for (int i = 0; i < TEST_MX_ALLOW_GENERATE_TIMES_OF_USER_WITH_DAY; i++) {
            Captcha captcha = captchaManager.generate(SimpleCaptchaType.MOBILE_PHONE, SimpleUseScene.LOGIN, owner);
            Assertions.assertNotNull(captcha);
        }
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> captchaManager.generate(SimpleCaptchaType.MOBILE_PHONE, SimpleUseScene.REGISTER, owner));
        Assertions.assertEquals("已超过每天允许发送的最大次数", exception.getMessage());
    }

    private void assertCaptchaError(Captcha.CaptchaType type, int maxAllowVerificationTimes) {
        for (Captcha.CaptchaUseScene scene : SimpleUseScene.values()) {
            String owner = RandomStringUtils.randomAlphanumeric(12);
            Captcha captcha = captchaManager.generate(type, scene, owner);
            Assertions.assertNotNull(captcha);
            String expected = RandomStringUtils.randomAlphanumeric(4);
            BaseException exception = Assertions.assertThrows(BaseException.class, () -> captchaManager.verify(expected, type, scene, owner));
            Assertions.assertEquals("$.captcha.verify.failure", exception.getMessage());
            Captcha next = captchaManager.getCaptchaStorage().get(captcha.getType(), captcha.getUseScene(), owner);
            if (maxAllowVerificationTimes <= 1) {
                Assertions.assertNull(next);
            } else {
                Assertions.assertNotNull(next);
            }
        }
    }

    private Collection<CaptchaContentProvider> getProviders() {
        return Arrays.asList(
                new PictureCaptchaContentProvider(new PictureCaptchaProperties(), new SimplePictureGenerator()),
                new MobilePhoneCaptchaContentProvider(new MobilePhoneCaptchaProperties()),
                new QrCodeCaptchaContentProvider(() -> "100", new QrCodeCaptchaProperties())
        );
    }

    private static CaptchaStorage getCaptchaStorage() {
        return new CacheCaptchaStorage(new ConcurrentMapCacheManager());
    }
}