package com.wind.payment.alipay;


import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付宝交易状态
 * WAIT_BUYER_PAY（交易创建，等待买家付款）、
 * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、
 * TRADE_SUCCESS（交易支付成功）
 * TRADE_FINISHED（交易结束，不可退款）
 *
 * @author wuxp
 * @date 2023-10-01 14:58
 */
@AllArgsConstructor
@Getter
public enum AliPayTransactionState implements DescriptiveEnum {

    WAIT_BUYER_PAY("交易创建，等待买家付款。"),

    TRADE_FINISHED("交易成功且结束"),

    TRADE_SUCCESS("交易成功"),

    TRADE_PENDING("等待卖家收款"),

    TRADE_CLOSED("交易关闭");

    private final String desc;

}