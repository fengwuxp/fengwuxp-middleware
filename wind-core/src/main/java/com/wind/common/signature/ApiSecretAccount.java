package com.wind.common.signature;

/**
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
}
