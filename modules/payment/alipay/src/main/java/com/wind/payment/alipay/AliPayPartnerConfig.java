package com.wind.payment.alipay;

import lombok.Data;

/**
 * 支付宝支付配置
 *
 * @author wuxp
 * @date 2023-10-01 08:55
 **/
@Data
public class AliPayPartnerConfig {

    /**
     * 支付宝加密类型
     */
    public enum EncryptType {

        RSA,

        RSA2;
    }

    /**
     * appId
     */
    private String appId;

    /**
     * 合作者(商户号)
     */
    private String partner;

    /**
     * 网关URL
     */
    private String serviceUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * rsa 私钥
     */
    private String rsaPrivateKey;

    /**
     * rsa 公钥
     */
    private String rsaPublicKey;

    /**
     * 加密类型：RSA、RSA2
     */
    private EncryptType encryptType = EncryptType.RSA2;

    /**
     * 字符编码
     */
    private String charset = "UTF-8";
}
