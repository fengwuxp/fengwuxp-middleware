package com.wind.mybatis.encrypt;

import com.wind.common.exception.AssertUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * dal 加解密 {@link org.apache.ibatis.type.TypeHandler} 支持
 *
 * @author wuxp
 * @date 2024-07-15 18:21
 **/
public abstract class AbstractEncryptBaseTypeHandler<T> extends BaseTypeHandler<T> {

    private static final AtomicReference<TextEncryptor> TEXT_ENCRYPTOR = new AtomicReference<>();

    public static void setTextEncryptor(TextEncryptor encryptor) {
        TEXT_ENCRYPTOR.set(encryptor);
    }

    static TextEncryptor requireTextEncryptor() {
        TextEncryptor result = TEXT_ENCRYPTOR.get();
        AssertUtils.notNull(result, "TextEncryptor not init");
        return result;
    }

}
