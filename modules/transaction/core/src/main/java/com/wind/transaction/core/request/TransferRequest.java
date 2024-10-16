package com.wind.transaction.core.request;


import com.wind.transaction.core.AccountTransactionType;
import com.wind.transaction.core.Money;
import com.wind.transaction.core.TransactionContextVariables;
import com.wind.transaction.core.WindTransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 预存款账户转入\转出请求
 *
 * @author wuxp
 * @date 2023-11-28 19:31
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    /**
     * 转入\转出金额
     */
    @NotNull
    private Money amount;

    /**
     * 关联的交易流水号 sn
     */
    @NotBlank
    @Size(max = 50)
    private String transactionSn;

    /**
     * 交易类型
     */
    @NotNull
    private AccountTransactionType transactionType;

    /**
     * 业务场景
     */
    @NotBlank
    @Size(max = 30)
    private String businessScene;

    /**
     * 描述（备注）
     */
    @Size(max = 300)
    private String description;

    /**
     * 上下文透传变量
     */
    private TransactionContextVariables contextVariables;
}
