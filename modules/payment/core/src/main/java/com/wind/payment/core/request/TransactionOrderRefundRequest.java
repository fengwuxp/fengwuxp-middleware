package com.wind.payment.core.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 交易订单退款
 *
 * @author wuxp
 * @date 2023-09-30 19:47
 **/
@Data
public class TransactionOrderRefundRequest implements Serializable {

    private static final long serialVersionUID = 8514897252783130486L;

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

    /**
     * 应用内的交易退款流水号
     */
    @NotBlank
    private String transactionRefundNo;

    /**
     * 退款金额
     * 单位：分
     */
    @NotNull
    @Min(value = 1)
    private Integer refundAmount;

    /**
     * 订单总金额
     * 单位：分
     */
    @NotNull
    @Min(value = 1)
    private Integer orderAmount;

    /**
     * 退款通知 url
     */
    @NotBlank
    private String refundNotifyUrl;

    /**
     * 退款原因
     */
    private String refundReason;
}
