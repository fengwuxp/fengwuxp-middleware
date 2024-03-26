package com.wind.payment.alipay.response;


import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * web page 支付返回结果
 *
 * @author wuxp
 * @date 2023-10-01 17:32
 **/
@Data
@Accessors(chain = true)
public class AliPayPageTransactionPayResult {

    /**
     * 用于唤起 支付宝 App 支付的字符串
     */
    private String orderInfo;

    /**
     * 应用内的交易流水号
     */
    @NotBlank
    private String transactionNo;

    /**
     * 第三方交易流水号
     */
    @NotBlank
    private String outTransactionNo;

    private String sellerId;

    private String totalAmount;

    private String tradeNo;
}
