package com.wind.transaction.core;

import com.wind.common.WindConstants;

/**
 * 这是一个可用于支出、收入的支付交易账户定义
 *
 * @author wuxp
 * @date 2023-12-01 10:37
 **/
public interface TransactionAccount {

    /**
     * @return 账户 id
     */
    TransactionAccountId getAccountId();

    /**
     * @return 账户所有者
     */
    String getOwner();

    /**
     * 账户转入（充值）累计数额
     *
     * @return 账户总转入数额 （总转入）
     */
    Integer getAmount();

    /**
     * 账户当下可用于支出的数额
     * 可用余额 = 总转入额度 + 总退款额度 - 总支出额度 - 已冻结额度 - 手续费
     *
     * @return 账户可用数额
     */
    default Integer getAvailableAmount() {
        return getAmount() + getRefundedAmount() - getExpensesAmount() - getFreezeAmount() - getFeeAmount();
    }

    /**
     * 累计账户由于未来某时刻支出需要临时冻结一部分余额，以保证在支付阶段不会由于 {@link #getAvailableAmount()} 不够导致支付失败
     *
     * @return 账户已冻结数额（已冻结）
     */
    Integer getFreezeAmount();

    /**
     * 由于支付、转出、提现等累计的数额
     *
     * @return 累计账户已支出的数额（总支出）
     */
    Integer getExpensesAmount();

    /**
     * 累计账户由于成功支付后、交易取消或部分取消退回账户的余额
     *
     * @return 已退款数额（总退款）
     */
    Integer getRefundedAmount();

    /**
     * 累计手续费
     *
     * @return 交易服务费用
     */
    default Integer getFeeAmount() {
        return 0;
    }

    /**
     * 账户金额的币种类型
     *
     * @return 币种类型
     */
    default CurrencyType getCurrencyType() {
        return CurrencyType.CNY;
    }

    /**
     * @return 卡是否可用
     */
    default boolean isAvailable() {
        return getAvailableAmount() > 0;
    }

    /**
     * @return 是否允许透支
     */
    default boolean isAllowOverdraft() {
        return false;
    }

    /**
     * @return 账户描述
     */
    default String getDesc() {
        return WindConstants.EMPTY;
    }

}
