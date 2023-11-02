package com.wind.security.crypto;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.rsa.crypto.RsaAlgorithm;
import org.springframework.security.rsa.crypto.RsaRawEncryptor;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

class AuthenticationParameterEncryptorTest {

    @Test
    void decrypt() {
        RsaSecretEncryptor encryptor = new RsaSecretEncryptor(genKeyPir(), RsaAlgorithm.OAEP);
        AuthenticationParameterEncryptor.ENCRYPT_PRINCIPAL.set(true);
        AuthenticationParameterEncryptor.ENCRYPTOR.set(encryptor);
        String principal = RandomStringUtils.randomAlphabetic(16);
        String credentials = RandomStringUtils.randomAlphabetic(16);
        AuthenticationParameterEncryptor.AuthenticationParameter encrypt = AuthenticationParameterEncryptor.encrypt(principal, credentials);
        AuthenticationParameterEncryptor.AuthenticationParameter decrypt = AuthenticationParameterEncryptor.decrypt(encrypt.getPrincipal(), encrypt.getCredentials());
        Assertions.assertEquals(principal, decrypt.getPrincipal());
        Assertions.assertEquals(credentials, decrypt.getCredentials());
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