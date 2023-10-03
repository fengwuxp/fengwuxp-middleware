package com.wind.payment.alipay;

import com.wind.payment.core.PaymentTransactionScene;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付宝支付场景
 *
 * @author wuxp
 * @date 2023-10-03 19:05
 **/
@AllArgsConstructor
@Getter
public enum AlipayPayScene implements PaymentTransactionScene {

    /**
     * app
     */
    APP("App支付"),

    /**
     * 付款码（或刷脸）
     */
    AUTH_CODE("付款码支付"),

    /**
     * 扫（收款）码
     */
    SCAN_QR_CODE("扫码支付"),

    /**
     * pc
     */
    WEB_PAGE("网页支付");


    private final String desc;
}
