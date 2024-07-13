package com.wind.security.crypto.symmetric;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotBlank;
import java.util.Base64;

/**
 * 对称加密：AES
 * AES（Advanced Encryption Standard，高级加密标准）是一种对称加密算法，即同一个密钥用于加密和解密。
 * AES被广泛用于保护数据的安全性。AES支持128位、192位和256位密钥长度，常见的块大小是128位。
 *
 * @author wuxp
 * @date 2024-07-13 11:00
 * @see org.springframework.security.crypto.encrypt.AesBytesEncryptor
 **/
public class AesTextEncryptor implements TextEncryptor {

    /**
     * AES 加密模式和填充
     */
    private final String transformation;

    /**
     * 初始化向量
     * IV（Initialization Vector，初始化向量）主要作用是增加加密的安全性，避免相同的明文在相同的密钥下得到相同的密文。
     * 随机化初始块：IV使得即使相同的明文在相同的密钥下加密，也会产生不同的密文。它为加密过程提供了随机性。
     * 防止模式攻击：使用IV可以防止某些类型的攻击（如词典攻击和重放攻击），提高加密的安全性。
     * 保持解密正确性：解密过程中需要使用相同的IV，确保解密得到正确的明文。
     */
    private final SecretKey secretKey;

    /**
     * 初始化向量
     */
    private final IvParameterSpec iv;

    private AesTextEncryptor(AESPaddingType paddingType, String base64Key, String ivBase64Text) {
        AssertUtils.notNull(paddingType, "argument paddingType must not empty");
        AssertUtils.hasText(base64Key, "argument key must not empty");
        this.transformation = paddingType.getTransformation();
        this.secretKey = decodeKeyFromText(base64Key);
        this.iv = StringUtils.hasText(ivBase64Text) ? decodeIVFromText(ivBase64Text) : null;
    }

    public static TextEncryptor defaults(@NotBlank String base64Key) {
        return new AesTextEncryptor(AESPaddingType.DEFAULT, base64Key, null);
    }

    public static TextEncryptor ebc(@NotBlank String base64Key) {
        return new AesTextEncryptor(AESPaddingType.DEFAULT, base64Key, null);
    }

    /**
     * 推荐使用
     *
     * @param base64Key    base64 编码后的加密秘钥
     * @param ivBase64Text 初始化向量，必须长度为 16 的 byte 数组 base64 编码后的字符串（长度为 24）
     * @return 加密器实例
     */
    public static TextEncryptor cbc(@NotBlank String base64Key, @NotBlank String ivBase64Text) {
        return new AesTextEncryptor(AESPaddingType.CBC, base64Key, ivBase64Text);
    }

    /**
     * AES 加密
     *
     * @param text 待加密的字符串
     * @return 加密结果，base64 编码
     */
    @Override
    public String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, this.iv);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "AES encrypt error", exception);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, this.iv);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "AES decrypt error", exception);
        }
    }

    /**
     * 将Base64编码的密钥字符串解码为SecretKey对象
     *
     * @param key 秘钥字符串
     * @return SecretKey
     */
    private SecretKey decodeKeyFromText(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, AesTextEncryptor.AESPaddingType.DEFAULT.getTransformation());
    }

    /**
     * 将Base64编码的IV字符串解码为IvParameterSpec对象
     *
     * @param iv 初始化向量字符串
     * @return IvParameterSpec
     */
    private IvParameterSpec decodeIVFromText(String iv) {
        byte[] decodedIV = Base64.getDecoder().decode(iv);
        return new IvParameterSpec(decodedIV);
    }

    /**
     * AES 加密填充类型
     */
    @AllArgsConstructor
    @Getter
    public enum AESPaddingType {

        /**
         * 默认
         */
        DEFAULT("AES"),

        /**
         * 模式：ECB（Electronic Codebook）
         * 填充：无填充
         * 特性：简单但不安全，容易受到模式攻击，不推荐使用。
         */
        ECB_NO_PADDING("AES/ECB/NoPadding"),

        /**
         * 模式：ECB（Electronic Codebook）
         * 填充：PKCS5Padding
         * 特性：比NoPadding安全，但ECB模式本身不推荐使用。
         */
        ECB("AES/ECB/PKCS5Padding"),

        /**
         * 模式：CBC（Cipher Block Chaining）
         * 填充：无填充
         * 特性：需要数据块是完整的块大小（通常是16字节）的倍数，否则会出错。
         */
        CBC_NO_PADDING("AES/CBC/NoPadding"),

        /**
         * 模式：CBC（Cipher Block Chaining）
         * 填充：PKCS5Padding
         * 特性：常用且安全，推荐使用。
         */
        CBC("AES/CBC/PKCS5Padding"),

        /**
         * 模式：OFB（Output Feedback）
         * 填充：无填充
         * 特性：类似CFB，适用于流数据，加密和解密过程中保持同步。
         */
        CFB_NO_PADDING("AES/CFB/NoPadding"),

        /**
         * 模式：CFB（Cipher Feedback）
         * 填充：PKCS5Padding
         * 特性：较少使用，CFB模式通常与NoPadding一起使用。
         */
        CFB("AES/CFB/PKCS5Padding"),

        /**
         * 模式：OFB（Output Feedback）
         * 填充：无填充
         * 特性：类似CFB，适用于流数据，加密和解密过程中保持同步。
         */
        OFB_NO_PADDING("AES/OFB/NoPadding"),

        /**
         * 模式：OFB（Output Feedback）
         * 填充：PKCS5Padding
         * 特性：较少使用，OFB模式通常与NoPadding一起使用。
         */
        OFB("AES/OFB/PKCS5Padding"),

        /**
         * 模式：GCM（Galois/Counter Mode）
         * 填充：无填充
         * 特性：提供认证和加密，推荐使用，特别适合需要数据完整性验证的场景。
         */
        GCM("AES/GCM/NoPadding");

        /**
         * 填充类型
         */
        private final String transformation;

    }
}
