package com.wind.security.crypto;

import lombok.Getter;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;

import java.util.function.Function;

/**
 * @author wuxp
 * @date 2024-07-13 12:46
 **/
public abstract class AbstractBytesKeyProvider<T> implements BytesKeyGenerator {

    private static final Function<byte[], byte[]> NONE = encryptedBytes -> encryptedBytes;

    /**
     * 秘钥字节数组
     */
    @Getter
    private final byte[] keyBytes;

    /**
     * 用于解密秘钥的 Decryptor
     */
    private final Function<byte[], byte[]> keyDecryptor;

    /**
     * 用于加载秘钥的参数
     */
    private final T loadKeyParam;

    public AbstractBytesKeyProvider(T loadKeyParam, Function<byte[], byte[]> keyDecryptor) {
        this.loadKeyParam = loadKeyParam;
        this.keyDecryptor = keyDecryptor;
        this.keyBytes = getKeyBytes();
    }

    public AbstractBytesKeyProvider(T loadKeyParam) {
        this(loadKeyParam, NONE);
    }

    @Override
    public int getKeyLength() {
        return keyBytes.length;
    }

    @Override
    public byte[] generateKey() {
        return keyBytes;
    }

    protected abstract byte[] loadKeyBytes(T loadKeyParam);

    private byte[] getKeyBytes() {
        byte[] bytes = loadKeyBytes(this.loadKeyParam);
        return keyDecryptor == null ? bytes : keyDecryptor.apply(bytes);
    }
}
