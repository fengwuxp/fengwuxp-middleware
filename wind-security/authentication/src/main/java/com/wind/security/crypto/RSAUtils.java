package com.wind.security.crypto;

import com.wind.common.exception.AssertUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * rsa 加密工具
 *
 * @author wuxp
 */
final class RSAUtils {

    private static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

    private RSAUtils() {
        throw new AssertionError();
    }

    public static String encryptAsText(String data, PublicKey publicKey) {
        return Base64Utils.encodeToString(encrypt(data, publicKey));
    }

    public static byte[] encrypt(String content, PublicKey publicKey) {
        AssertUtils.notNull(content, "bytes must not null");
        AssertUtils.notNull(publicKey, "private key must not null");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(content.getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Encrypt failed!", e);
        }
    }

    public static String decrypt(String data, PrivateKey privateKey) {
        return decrypt(Base64Utils.decodeFromString(data), privateKey);
    }

    public static String decrypt(byte[] bytes, PrivateKey privateKey) {
        AssertUtils.notNull(bytes, "bytes must not null");
        AssertUtils.notNull(privateKey, "private key must not null");
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            throw new IllegalArgumentException("Decrypt failed!", e);
        }
    }
}