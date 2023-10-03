package com.wind.payment.wechat;

import com.wind.payment.core.PaymentTransactionScene;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信支付场景
 *
 * @author wuxp
 * @date 2023-10-03 18:03
 **/
@AllArgsConstructor
@Getter
public enum WechatPayScene implements PaymentTransactionScene {

    JSAPI("公众号支付"),

    NATIVE("扫码支付"),


    APP("APP支付"),

    MWEB("网页支付");

    private final String desc;
}
