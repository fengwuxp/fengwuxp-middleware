package com.wind.core.api.signature;

import java.util.Objects;

/**
 * Api 访问的服务账号
 *
 * @author wuxp
 * @date 2024-02-02 16:11
 **/
public interface ApiSecretAccount {

    /**
     * @return 访问 key
     */
    String getAccessKey();

    /**
     * @return 获取签名密钥
     */
    String getSecretKey();

    static ApiSecretAccount immutable(String accessKey, String secretKey) {
        Objects.requireNonNull(accessKey, "argument accessKey must not null");
        Objects.requireNonNull(secretKey, "argument secretKey must not null");
        return new ApiSecretAccount() {
            @Override
            public String getAccessKey() {
                return accessKey;
            }

            @Override
            public String getSecretKey() {
                return secretKey;
            }
        };
    }
}
