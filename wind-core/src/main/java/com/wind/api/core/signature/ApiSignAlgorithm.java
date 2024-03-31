package com.wind.api.core.signature;

import com.wind.signature.algorithm.HmacSHA256Signer;
import com.wind.signature.algorithm.Sha256WithRsaSigner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author wuxp
 * @date 2024-03-05 09:57
 **/
@Getter
@AllArgsConstructor
public enum ApiSignAlgorithm implements ApiSigner {

    /**
     * 摘要签名
     * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/qal4b72cxw84cu6g
     */
    HMAC_SHA256("HmacSHA256", new ApiSigner() {
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
    }),

    /**
     * 参见：https://www.yuque.com/suiyuerufeng-akjad/wind/qal4b72cxw84cu6g
     */
    SHA256_WITH_RSA("SHA256WithRSA", new ApiSigner() {

        @Override
        public String sign(ApiSignatureRequest request, String privateKey) {
            return Sha256WithRsaSigner.sign(request.getSignTextForSha256WithRsa(), privateKey);
        }

        @Override
        public boolean verify(ApiSignatureRequest request, String publicKey, String sign) {
            return Sha256WithRsaSigner.verify(request.getSignTextForSha256WithRsa(), publicKey, sign);
        }
    }),
    ;

    private final String algorithm;

    private final ApiSigner delegate;


    @Override
    public String sign(ApiSignatureRequest request, String secretKey) {
        return delegate.sign(request, secretKey);
    }

    @Override
    public boolean verify(ApiSignatureRequest request, String secretKey, String sign) {
        return delegate.verify(request, secretKey, sign);
    }
}
