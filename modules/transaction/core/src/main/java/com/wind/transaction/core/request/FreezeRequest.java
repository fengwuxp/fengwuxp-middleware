package com.wind.transaction.core.request;


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
 * 冻结，解冻请求
 *
 * @author wuxp
 * @date 2023-11-28 19:38
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FreezeRequest {

    /**
     * 冻结/解冻金额
     */
    @NotNull
    private Money amount;

    /**
     * 关联的交易流水号，可空
     */
    @Size(max = 50)
    private String transactionSn;

    /**
     * 交易类型
     */
    @NotNull
    private WindTransactionType transactionType;

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
