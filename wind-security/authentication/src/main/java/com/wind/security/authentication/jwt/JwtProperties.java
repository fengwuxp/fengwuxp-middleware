package com.wind.security.authentication.jwt;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;

import java.beans.Transient;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;


/**
 * @author wuxp
 * <a href="https://llbetter.com/JWT-default-claims/">JWT(Json Web Token)中默认的声明含义</a>
 */
@Data
public class JwtProperties {

    /**
     * jwt issuer
     * jwt的颁发者，其值应为大小写敏感的字符串或Uri。
     */
    private String issuer;

    /**
     * jwt audience
     * jwt的适用对象，其值应为大小写敏感的字符串或Uri。一般可以为特定的App、服务或模块
     */
    private String audience;

    /**
     * Generate token to set expire time
     */
    private Duration effectiveTime = Duration.ofHours(4);

    /**
     * refresh jwt token 有效天数
     */
    private Duration refreshEffectiveTime = Duration.ofDays(3);

    /**
     * token 请求头名称
     */
    private String headerName = HttpHeaders.AUTHORIZATION;

    /**
     * rsa 公钥
     */
    private String rsaPublicKey;

    /**
     * rsa 私钥
     */
    private String rsaPrivateKey;

    private Class<? extends JwtUser> userType = JwtUser.class;

    @Transient
    KeyPair getKeyPair() {
        try {
            return new KeyPair(getPublicKey(), getPrivateKey());
        } catch (Exception exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "生成 Rsa 秘钥对失败", exception);
        }
    }

    private PublicKey getPublicKey() throws Exception {
        byte[] bytes = Base64Utils.decodeFromString(getRsaPublicKey());
        return getRsaFactory().generatePublic(new X509EncodedKeySpec(bytes));
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] bytes = Base64Utils.decodeFromString(getRsaPrivateKey());
        return getRsaFactory().generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static KeyFactory getRsaFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }

}
