package com.wind.security;

import com.wind.security.crypto.FileBytesKeyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileCopyUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author wuxp
 * @date 2024-07-13 13:06
 **/
class FileBytesKeyProviderTests {

    private String filepath;

    private String secretKey;

    @BeforeEach
    void setup() throws Exception {
        secretKey = genSecretKey();
        String base = Objects.requireNonNull(FileBytesKeyProviderTests.class.getResource("/")).getFile();
        filepath = Paths.get(base, "test-secretkey").toString();
        File file = new File(filepath);
        if (!file.exists()) {
            Assertions.assertTrue(file.createNewFile());
        }
        FileCopyUtils.copy(secretKey.getBytes(StandardCharsets.UTF_8), Files.newOutputStream(file.toPath()));
    }

    @Test
    void testGenerateKey() {
        Function<String, byte[]> keyDecryptor = String::getBytes;
        FileBytesKeyProvider provider = new FileBytesKeyProvider(filepath, keyDecryptor);
        byte[] bytes = provider.generateKey();
        Assertions.assertEquals(keyDecryptor.apply(secretKey), new String(bytes));
    }


    private String genSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        // 256位密钥
        keyGen.init(256);
        // 生成密钥
        SecretKey secretKey = keyGen.generateKey();
        // 将密钥编码为Base64字符串
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

}
