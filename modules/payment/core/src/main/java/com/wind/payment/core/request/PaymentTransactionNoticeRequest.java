package com.wind.payment.core.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付交易通知请求对象
 *
 * @author wuxp
 * @date 2023-10-01 13:43
 **/
@Data
public class PaymentTransactionNoticeRequest implements Serializable {

    private static final long serialVersionUID = 7026788394092470016L;

    /**
     * 应用内的交易流水号
     */
    @NotBlank
    private String transactionNo;

    /**
     * 订单总金额
     * 单位：分
     */
    @NotNull
    private Integer orderAmount;

    /**
     * 原始的通知请求参数
     */
    private Object rawRequest;

    @SuppressWarnings("unchecked")
    public <T> T getRawRequest() {
        return (T) rawRequest;
    }
}
