package com.wind.payment.core.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 查询交易（支付）订单
 *
 * @author wuxp
 * @date 2023-09-30 19:39
 **/
@Data
public class QueryTransactionOrderRequest implements Serializable {

    private static final long serialVersionUID = -4921074854542888262L;

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
}
