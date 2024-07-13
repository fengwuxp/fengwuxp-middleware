package com.wind.security;

import com.wind.security.crypto.EnvBytesKeyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author wuxp
 * @date 2024-07-13 13:03
 **/
class EnvBytesKeyProviderTests {

    private static final String ENV_NAME = "test-key";

    @BeforeEach
    void setup() {
        System.setProperty(ENV_NAME, ENV_NAME);
    }

    @Test
    void testGenerateKey() {
        byte[] bytes = new EnvBytesKeyProvider(ENV_NAME).generateKey();
        Assertions.assertEquals(ENV_NAME, new String(bytes));
    }
}
