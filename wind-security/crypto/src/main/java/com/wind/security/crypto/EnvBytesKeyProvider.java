package com.wind.security.crypto;

import com.wind.common.exception.BaseException;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * 通过环境变量或 -D 参数加载秘钥
 *
 * @author wuxp
 * @date 2024-07-13 12:45
 **/
public class EnvBytesKeyProvider extends AbstractBytesKeyProvider<String> {

    public EnvBytesKeyProvider(String name, Function<byte[], byte[]> keyDecryptor) {
        super(name, keyDecryptor);
    }

    public EnvBytesKeyProvider(String name) {
        super(name);
    }

    @Override
    protected byte[] loadKeyBytes(String name) {
        return getProperty(name).getBytes(StandardCharsets.UTF_8);
    }

    private static String getProperty(String name) {
        String result = System.getenv(name);
        result = StringUtils.hasText(result) ? result : System.getProperty(name, result);
        if (StringUtils.hasText(result)) {
            return result;
        }
        throw BaseException.common(String.format("name = %s env not found", name));
    }
}
