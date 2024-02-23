package com.wind.core.api.signature;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * Api 访问的服务账号
 *
 * @author wuxp
 * @date 2024-02-02 16:11
 **/
public interface ApiSecretAccount {

    /**
     * @return 账号唯一标识
     */
    @NotBlank
    String getId();

    /**
     * @return 获取签名密钥
     */
    @NotBlank
    String getSecretKey();

    static ApiSecretAccount immutable(String accountId, String secretKey) {
        Objects.requireNonNull(accountId, "argument accountId must not null");
        Objects.requireNonNull(secretKey, "argument secretKey must not null");
        return new ApiSecretAccount() {
            @Override
            public String getId() {
                return accountId;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }
        };
    }
}
