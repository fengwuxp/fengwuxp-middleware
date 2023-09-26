package com.wind.security.authentication.jwt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Base64Utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

class JwtTokenCodecTest {

    private final JwtTokenCodec jwtTokenCodec = new JwtTokenCodec(jwtProperties());


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
        String id = jwtTokenCodec.parseRefreshToken(token);
        Assertions.assertEquals("1", id);
    }

    private JwtProperties jwtProperties() {
        KeyPair keyPair = genKeyPir();
        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
        JwtProperties result = new JwtProperties();
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