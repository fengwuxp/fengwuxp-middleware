package com.wind.security.crypto;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * @author wuxp
 * @date 2024-07-15 13:06
 **/
public class CacheableBytesKeyProvider extends AbstractBytesKeyProvider<String>{

    public CacheableBytesKeyProvider(String key, Function<String, byte[]> keyDecryptor) {
        super(key, keyDecryptor);
    }

    public CacheableBytesKeyProvider(String key) {
        super(key);
    }

    @Override
    protected String loadKey(String key) {
        return key;
    }
}
