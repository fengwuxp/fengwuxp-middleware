package com.wind.security.crypto;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 * 从系统文件加载秘钥
 *
 * @author wuxp
 * @date 2024-07-13 12:41
 **/
public final class FileBytesKeyProvider extends AbstractBytesKeyProvider<String> {

    public FileBytesKeyProvider(String filepath, Function<byte[], byte[]> keyDecryptor) {
        super(filepath, keyDecryptor);
    }

    public FileBytesKeyProvider(String filepath) {
        super(filepath);
    }

    @Override
    protected byte[] loadKeyBytes(String filepath) {
        try {
            return FileCopyUtils.copyToByteArray(Files.newInputStream(Paths.get(filepath)));
        } catch (IOException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "load key error", exception);
        }
    }
}
