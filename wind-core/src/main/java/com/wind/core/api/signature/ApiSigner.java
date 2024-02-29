package com.wind.core.api.signature;

import com.wind.core.signature.HmacSHA256Signer;
import com.wind.core.signature.Sha256WithRsaSigner;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Api Signer
 *
 * @author wuxp
 * @date 2024-02-23 12:48
 **/
public interface ApiSigner {


    /**
     * 摘要签名
     * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/qal4b72cxw84cu6g
     */
    ApiSigner HMAC_SHA256 = new ApiSigner() {

        /**
         * 签名验证
         *
         * @param request 用于验证签名的请求
         * @param sign    待验证的签名
         * @return 签名验证是否通过
         */
        @Override
        public boolean verify(ApiSignatureRequest request, String secretKey, String sign) {
            return Objects.equals(sign(request, secretKey), sign);
        }


        @Override
        public String sign(ApiSignatureRequest request, String secretKey) {
            return HmacSHA256Signer.sign(request.getSignTextForDigest(), secretKey);
        }
    };

    /**
     * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/qal4b72cxw84cu6g
     */
    ApiSigner SHA256_WITH_RSA = new ApiSigner() {

        @Override
        public String sign(ApiSignatureRequest request, String secretKey) {
            return Sha256WithRsaSigner.sign(request.getSignTextForSha256WithRsa(), secretKey);
        }

        @Override
        public boolean verify(ApiSignatureRequest request, String secretKey, String sign) {
            return Sha256WithRsaSigner.verify(request.getSignTextForSha256WithRsa(), secretKey, sign);
        }
    };


    /**
     * 生成签名
     *
     * @param request   签名请求
     * @param secretKey 签名秘钥
     * @return 签名结果
     */
    @NotNull
    String sign(@NotNull ApiSignatureRequest request, String secretKey);

    /**
     * 签名验证
     *
     * @param request   用于验证签名的请求
     * @param secretKey 签名秘钥
     * @param sign      待验证的签名
     * @return 签名验证是否通过
     */
    boolean verify(@NotNull ApiSignatureRequest request, String secretKey, @NotNull String sign);
}
