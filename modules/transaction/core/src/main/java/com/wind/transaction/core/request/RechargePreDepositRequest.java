package com.wind.transaction.core.request;


import com.wind.transaction.core.Money;
import com.wind.transaction.core.TransactionContextVariables;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预存款充值请求
 *
 * @author wuxp
 * @date 2023-11-28 19:44
 **/
@Data
@Accessors(chain = true)
public class RechargePreDepositRequest {

    /**
     * 用户ID
     */
    @NotNull
    private Object userId;

    /**
     * 收款方标识
     */
    @NotBlank
    private String payeeId;

    /**
     * 付款方标识
     */
    @NotBlank
    private String payerId;

    /**
     * 充值方式标识
     */
    @NotNull
    private String paymentMethod;

    /**
     * 第三方或银行交易流水号，可空
     * 如果是通过第三方平台做的充值，则必填
     */
    @Size(max = 80)
    private String outTransactionSn;

    /**
     * 充值金额
     */
    @NotNull
    private Money amount;

    /**
     * 描述（备注）
     */
    @Size(max = 150)
    private String description;

    /**
     * 上下文透传变量
     */
    @NotNull
    private TransactionContextVariables contextVariables;

    @SuppressWarnings("unchecked")
    public <T> T getUserId() {
        return (T) userId;
    }

}
