package com.wind.transaction.core;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易类型
 *
 * @author wuxp
 * @date 2024-05-14 16:44
 **/
@AllArgsConstructor
@Getter
public enum WindTransactionType implements DescriptiveEnum {

    /**
     * 充值
     */
    RECHARGE("充值"),

    /**
     * 支付
     */
    PAYMENT("支付"),

    /**
     * 退款
     */
    REFUND("退款"),


    /**
     * 手续费
     */
    FEE("手续费");


    private final String desc;
}
