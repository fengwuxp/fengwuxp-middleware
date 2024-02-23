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

    static ApiSecretAccount immutable(String accessId, String secretKey) {
        Objects.requireNonNull(accessId, "argument accessId must not null");
        Objects.requireNonNull(secretKey, "argument secretKey must not null");
        return new ApiSecretAccount() {
            @Override
            public String getAccessId() {
                return accessId;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }
        };
    }
}
