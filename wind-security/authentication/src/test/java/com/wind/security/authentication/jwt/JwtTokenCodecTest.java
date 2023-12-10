package com.wind.security.authentication.jwt;

import com.wind.common.exception.BaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Collections;

class JwtTokenCodecTest {

    private final JwtTokenCodec jwtTokenCodec = new JwtTokenCodec(jwtProperties(null));

    @Test
    void testCodecUserToken() {
        JwtUser user = new JwtUser(1L, "", Collections.emptyMap());
        JwtToken token = jwtTokenCodec.encoding(user);
        JwtUser result = jwtTokenCodec.parse(token.getTokenValue()).getUser();
        Assertions.assertEquals(user, result);
    }

    @Test
    void testCodecRefreshToken() {
        JwtToken token = jwtTokenCodec.encodingRefreshToken(1L);
        Assertions.assertEquals(1L, token.getUserId());
    }

    @Test
    void testTokenExpire() throws Exception {
        JwtTokenCodec codec = new JwtTokenCodec(jwtProperties(Duration.ofMillis(1)));
        JwtToken token = codec.encodingRefreshToken(1L);
        Thread.sleep(100);
        String tokenValue = token.getTokenValue();
        BaseException exception = Assertions.assertThrows(BaseException.class, () -> jwtTokenCodec.parseRefreshToken(tokenValue));
        Assertions.assertEquals("登录令牌已失效，请重新登陆", exception.getMessage());
    }


    private JwtProperties jwtProperties(Duration duration) {
        KeyPair keyPair = genKeyPir();
        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
        JwtProperties result = new JwtProperties();
        if (duration != null) {
            result.setEffectiveTime(duration);
            result.setRefreshEffectiveTime(duration);
        }
        result.setIssuer("test");
        result.setAudience("test");
        result.setRsaPublicKey(publicKey);
        result.setRsaPrivateKey(privateKey);
        return result;
    }

    private KeyPair genKeyPir() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}