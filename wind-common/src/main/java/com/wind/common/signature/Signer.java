package com.wind.common.signature;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * 签名验证
 *
 * @author wuxp
 * @date 2023-10-18 22:08
 */
public final class Signer {

    private static final String DEFAULT_ALGORITHM = "HmacSHA256";

    /**
     * 基于 sha256 算法
     */
    public static final Signer SHA256 = new Signer(DEFAULT_ALGORITHM);

    /**
     * algorithm the standard name of the requested MAC algorithm
     */
    private final String algorithm;

    public Signer(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 签名验证
     *
     * @param sign    待验证的签名
     * @param request 用于验证签名的请求
     * @return 签名验证是否成功
     */
    public boolean verifySign(String sign, SignatureRequest request) {
        return Objects.equals(sign(request), sign);
    }

    /**
     * 生成签名
     *
     * @param request 签名请求
     * @return 签名内容
     */
    public String sign(SignatureRequest request) {
        return sign(request.getSignText(), request.getSecretKey());
    }

    /**
     * 生成签名
     *
     * @param signText  用于生成签名的字符串
     * @param secretKey 签名秘钥
     * @return 签名内容
     */
    private String sign(String signText, String secretKey) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            byte[] appSecretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            mac.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, algorithm));
            byte[] md5Result = mac.doFinal(signText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(md5Result);
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "签名验失败", exception);
        }
    }
}
