package com.wind.security.crypto.symmetric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * @author wuxp
 * @date 2024-07-13 11:48
 **/
class AesTextEncryptorTests {

    private static final String TEXT = "902O34M1294MR2/...234P12O4MDSJ1U23NKMSDFA[O3K2M42LFKDISU  N ,DSK";

    private final AesTextEncryptor encryptor = new AesTextEncryptor("test1231231", "li123k1o3", KeyGenerators.secureRandom(16));

    @Test
    void testCbc() throws Exception {
        String encrypt = encryptor.encrypt(TEXT);
        Assertions.assertEquals(TEXT, encryptor.decrypt(encrypt));
    }

}
