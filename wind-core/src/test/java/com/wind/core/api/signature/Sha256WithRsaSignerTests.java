package com.wind.core.api.signature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * @author wuxp
 * @date 2024-02-21 18:26
 **/
class Sha256WithRsaSignerTests {

    @Test
    void testSign() {
        KeyPair keyPair = genKeyPir();
        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
        String signText = "9912312313klamfl234;23.142942319,.1231023kmsdj19i23k12msdjh7u241po;pmdf8su90[3o2km,l;dsmk";
        String sign = Sha256WithRsaSigner.sign(signText, privateKey);
        Assertions.assertTrue(Sha256WithRsaSigner.verify(signText, publicKey, sign));
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
