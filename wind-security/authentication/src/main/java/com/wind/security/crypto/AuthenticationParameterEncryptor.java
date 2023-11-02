package com.wind.security.crypto;

import com.wind.common.annotations.VisibleForTesting;
import com.wind.security.authentication.WindAuthenticationProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.rsa.crypto.RsaAlgorithm;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 非常规用法，主要是为了方便使用
 * 鉴权参数加加解密者
 *
 * @author wuxp
 * @date 2023-11-02 20:24
 **/
@Slf4j
public final class AuthenticationParameterEncryptor implements ApplicationListener<ApplicationStartedEvent> {

    /**
     * 是否加解密 Principal
     */
    @VisibleForTesting
    static final AtomicBoolean ENCRYPT_PRINCIPAL = new AtomicBoolean(false);

    /**
     * 加密器
     */
    @VisibleForTesting
    static final AtomicReference<TextEncryptor> ENCRYPTOR = new AtomicReference<>();

    /**
     * 认证相关参数（公钥加密）
     *
     * @param principal   登录主体，例如：手机号码、用户名、邮箱等
     * @param credentials 登录证书，例如：密码、验证码等
     * @return 认证参数
     */
    public static AuthenticationParameter encrypt(String principal, String credentials) {
        TextEncryptor encryptor = ENCRYPTOR.get();
        if (encryptor != null) {
            if (ENCRYPT_PRINCIPAL.get()) {
                principal = encryptor.encrypt(principal);
            }
            credentials = encryptor.encrypt(credentials);
        }
        return new AuthenticationParameter(principal, credentials);
    }

    /**
     * 解密认证相关参数（私钥加密）
     *
     * @param principal   登录主体，例如：手机号码、用户名、邮箱等
     * @param credentials 登录验证密码
     * @return 认证参数
     */
    public static AuthenticationParameter decrypt(String principal, String credentials) {
        TextEncryptor encryptor = ENCRYPTOR.get();
        if (encryptor != null) {
            if (ENCRYPT_PRINCIPAL.get()) {
                principal = encryptor.decrypt(principal);
            }
            credentials = encryptor.decrypt(credentials);
        }
        return new AuthenticationParameter(principal, credentials);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            WindAuthenticationProperties properties = event.getApplicationContext().getBean(WindAuthenticationProperties.class);
            ENCRYPTOR.set(new RsaSecretEncryptor(properties.getKeyPair(), RsaAlgorithm.OAEP));
            ENCRYPT_PRINCIPAL.set(properties.isEncryptPrincipal());
        } catch (BeansException e) {
            log.info("un enable authentication parameter crypto");
        }
    }

    @AllArgsConstructor
    @Getter
    public static class AuthenticationParameter {

        /**
         * 登录主体，例如：手机号码、用户名、邮箱等
         */
        private final String principal;

        /**
         * 登录证书，例如：密码、验证码等
         */
        private final String credentials;
    }
}
