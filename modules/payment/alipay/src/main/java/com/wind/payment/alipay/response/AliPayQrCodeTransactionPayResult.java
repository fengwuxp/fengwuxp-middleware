package com.wind.payment.alipay.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 二维码支付
 *
 * @author wuxp
 * @date 2023-10-01 19:09
 **/
@Data
@AllArgsConstructor
public class AliPayQrCodeTransactionPayResult {

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 第三方交易流水号
     */
    @NotBlank
    private String outTransactionNo;
}
