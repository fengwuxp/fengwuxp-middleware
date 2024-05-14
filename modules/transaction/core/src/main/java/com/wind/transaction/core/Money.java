package com.wind.transaction.core;

import com.wind.common.exception.AssertUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用于描述货币的对象，包括货币数额和币种
 *
 * @author wuxp
 * @date 2023-10-06 09:01
 **/
@Getter
@EqualsAndHashCode
public final class Money implements Serializable {

    private static final long serialVersionUID = -7696239148769634763L;

    /**
     * 金额，单位：分
     */
    private final int amount;

    /**
     * 币种
     */
    private final CurrencyType currencyType;

    private Money(int amount, CurrencyType currencyType) {
        AssertUtils.isTrue(amount >= 0, "amount must greater than 0");
        AssertUtils.notNull(currencyType, "argument currencyType must not null");
        this.amount = amount;
        this.currencyType = currencyType;
    }

    /**
     * 转换为标准币种显示格式
     * eg: $10
     *
     * @return $10
     */
    public String asText() {
        return String.format("%s%s", currencyType.getSign(), this.fen2Yuan());
    }

    /**
     * money 加法
     *
     * @param money 被加的金额
     * @return 相加后的金额
     */
    public Money plus(Money money) {
        AssertUtils.isTrue(currencyType.equals(money.getCurrencyType()), "币种不一致");
        return Money.immutable(amount + money.getAmount(), currencyType);
    }

    /**
     * money 减法
     *
     * @param money 被减的金额
     * @return 相减后的金额
     */
    public Money subtract(Money money) {
        AssertUtils.isTrue(currencyType.equals(money.getCurrencyType()), "币种不一致");
        return Money.immutable(amount - money.getAmount(), currencyType);
    }

    /**
     * 获取货币Decimal
     */
    public BigDecimal fen2Yuan() {
        return BigDecimal.valueOf(amount).scaleByPowerOfTen(-currencyType.getPrecision());
    }

    /**
     * 创建一个具有{@param amount} 数额的货币对象
     *
     * @param amount       数额(分)
     * @param currencyType 货币类型
     * @return 货币实例
     */
    public static Money immutable(int amount, @NotNull CurrencyType currencyType) {
        return new Money(amount, currencyType);
    }


    /**
     * 元转分：创建一个具有{@param amount} 数额的货币对象
     *
     * @param amount       数额(元)
     * @param currencyType 货币类型
     * @return 货币实例
     */
    public static Money immutable(@NotNull BigDecimal amount, @NotNull CurrencyType currencyType) {
        AssertUtils.notNull(amount, "argument amount must not null");
        int longAmount = amount.scaleByPowerOfTen(currencyType.getPrecision()).intValue();
        return immutable(longAmount, currencyType);
    }
}
