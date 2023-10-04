package com.wind.security.authentication.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.BadJwtException;
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
        User user = new User("张三", "****", Collections.emptyList());
        String token = jwtTokenCodec.encoding("1", user);
        User result = jwtTokenCodec.parse(token, User.class).getUser();
        Assertions.assertEquals(user, result);
    }

    @Test
    void testCodecRefreshToken() {
        String token = jwtTokenCodec.encodingRefreshToken("1");
        JwtTokenPayload payload = jwtTokenCodec.parseRefreshToken(token);
        Assertions.assertEquals("1", payload.getUserId());
    }

    @Test
    void testTokenExpire() throws Exception {
        JwtTokenCodec codec = new JwtTokenCodec(jwtProperties(Duration.ofMillis(1)));
        String token = codec.encodingRefreshToken("1");
        Thread.sleep(100);
        BadJwtException exception = Assertions.assertThrows(BadJwtException.class, () -> jwtTokenCodec.parseRefreshToken(token));
        Assertions.assertEquals("An error occurred while attempting to decode the Jwt: Signed JWT rejected: Invalid signature", exception.getMessage());
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