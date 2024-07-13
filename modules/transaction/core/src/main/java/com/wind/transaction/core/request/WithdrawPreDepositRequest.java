package com.wind.transaction.core.request;


import com.wind.transaction.core.Money;
import com.wind.transaction.core.TransactionContextVariables;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预存款提现请求
 *
 * @author wuxp
 * @date 2023-11-28 20:05
 **/
@Data
@Accessors(chain = true)
public class WithdrawPreDepositRequest {

    /**
     * 用户ID
     */
    @NotNull
    private Object userId;

    /**
     * 提现付款方标识
     */
    @NotBlank
    private String payerId;

    /**
     * 提现收款方式标识
     */
    @NotBlank
    private String payeeId;

    /**
     * 提现收款方式
     */
    @NotNull
    private String payeeMethod;

    /**
     * 第三方或银行交易流水号，可空
     * 如果是通过第三方平台做的充值，则必填
     */
    private String outTransactionSn;

    /**
     * 提现金额
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
    private TransactionContextVariables contextVariables;

    @SuppressWarnings("unchecked")
    public <T> T getUserId() {
        return (T) userId;
    }

}
