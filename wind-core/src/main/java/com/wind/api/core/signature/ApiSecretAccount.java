package com.wind.api.core.signature;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Api 访问的服务账号
 *
 * @author wuxp
 * @date 2024-02-02 16:11
 **/
public interface ApiSecretAccount {

    /**
     * AccessKey or AppId
     *
     * @return 账号访问唯一标识
     */
    @NotBlank
    String getAccessId();

    /**
     * @return 获取签名密钥
     */
    @NotBlank
    String getSecretKey();

    /**
     * @return 签名算法
     */
    @NotNull
     ApiSignAlgorithm getSignAlgorithm();

    static ApiSecretAccount immutable(String accessId, String secretKey, ApiSignAlgorithm signer) {
        Objects.requireNonNull(accessId, "argument accessId must not null");
        Objects.requireNonNull(secretKey, "argument secretKey must not null");
        Objects.requireNonNull(signer, "argument signer must not null");
        return new ApiSecretAccount() {
            @Override
            public String getAccessId() {
                return accessId;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }

            @Override
            public ApiSignAlgorithm getSignAlgorithm() {
                return signer;
            }
        };
    }
}
