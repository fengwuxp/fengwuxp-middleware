package com.wind.payment.core;

/**
 * 支付交易插件工厂
 *
 * @author wuxp
 * @date 2023-09-30 19:21
 **/
public interface PaymentTransactionPluginFactory {

    /**
     * 创建一个支付交易插件
     *
     * @param partnerConfigOrId 商户配置 OR 唯一标识
     * @param platform          支付平台
     * @param scene             支付场景
     * @return 支付插件
     */
    PaymentTransactionPlugin factory(String partnerConfigOrId, PaymentTransactionPlatform platform, PaymentTransactionScene scene);
}
