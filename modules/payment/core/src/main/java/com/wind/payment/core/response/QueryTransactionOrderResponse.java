package com.wind.payment.core.response;

import com.wind.payment.core.enums.PaymentTransactionState;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 执行支付订单响应
 *
 * @author wuxp
 * @date 2023-09-30 19:41
 **/
@Data
@Accessors(chain = true)
public class QueryTransactionOrderResponse implements Serializable {

    private static final long serialVersionUID = -749648626405632029L;

    /**
     * 支付交易流水号(系统内的)
     */
    @NotNull
    private String transactionNo;

    /**
     * 第三方交易流水号
     */
    private String outTransactionNo;

    /**
     * 订单金额
     * 单位：分
     */
    private Integer orderAmount;

    /**
     * 实付金额
     * 单位：分
     * 买家实际付款的金额
     */
    private Integer buyerPayAmount;

    /**
     * 实收金额
     * 单位分
     * 该金额为本笔交易，商户账户能够实际收到的金额
     */
    private Integer receiptAmount;

    /**
     * 是否沙箱环境
     */
    private Boolean useSandboxEnv = false;

    /**
     * 交易状态
     */
    private PaymentTransactionState transactionState;

    /**
     * 付款账号
     */
    private String payerAccount;

    /**
     * 原始响应
     */
    private Object rawResponse;

    @SuppressWarnings("unchecked")
    public <T> T getRawResponse() {
        return (T) rawResponse;
    }
}
