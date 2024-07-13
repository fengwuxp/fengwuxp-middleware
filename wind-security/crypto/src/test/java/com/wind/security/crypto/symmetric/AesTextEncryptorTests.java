package com.wind.security.crypto.symmetric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author wuxp
 * @date 2024-07-13 11:48
 **/
class AesTextEncryptorTests {

    private static final String TEXT = "902O34M1294MR2/...234P12O4MDSJ1U23NKMSDFA[O3K2M42LFKDISU  N ,DSK";

    @Test
    void testDefaults() throws Exception {
        String secretKey = genSecretKey();
        TextEncryptor encryptor = AesTextEncryptor.defaults(secretKey);
        String encrypt = encryptor.encrypt(TEXT);
        Assertions.assertEquals(TEXT, encryptor.decrypt(encrypt));
    }

    @Test
    void testEbc() throws Exception {
        String secretKey = genSecretKey();
        TextEncryptor encryptor = AesTextEncryptor.ebc(secretKey);
        String encrypt = encryptor.encrypt(TEXT);
        Assertions.assertEquals(TEXT, encryptor.decrypt(encrypt));
    }

    @Test
    void testCbc() throws Exception {
        String secretKey = genSecretKey();
        TextEncryptor encryptor = AesTextEncryptor.cbc(secretKey, genIv());
        String encrypt = encryptor.encrypt(TEXT);
        Assertions.assertEquals(TEXT, encryptor.decrypt(encrypt));
    }

    private  String genSecretKey() throws Exception {
        // 初始化AES密钥生成器，指定密钥大小（128、192或256位）
        KeyGenerator keyGen = KeyGenerator.getInstance(AesTextEncryptor.AESPaddingType.DEFAULT.getTransformation());
        // 256位密钥
        keyGen.init(256);
        // 生成密钥
        SecretKey secretKey = keyGen.generateKey();
        // 将密钥编码为Base64字符串
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    private String genIv() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }
}
