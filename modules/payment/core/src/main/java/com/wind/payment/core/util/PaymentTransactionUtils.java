package com.wind.payment.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author wuxp
 * @date 2023-10-01 09:05
 **/
public final class PaymentTransactionUtils {

    private static final BigDecimal MULTIPLIER = new BigDecimal(100);

    private PaymentTransactionUtils() {
        throw new AssertionError();
    }

    /**
     * 分转换为元
     *
     * @param fee 分
     * @return 元
     */
    public static BigDecimal feeToYun(Integer fee) {
        return new BigDecimal(fee).divide(MULTIPLIER, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 元转为分
     *
     * @param yuan 元
     * @return 分
     */
    public static int yuanToFee(String yuan) {
        return yuanToFee(new BigDecimal(yuan));
    }

    /**
     * 元转为分
     *
     * @param yuan 元
     * @return 分
     */
    public static int yuanToFee(BigDecimal yuan) {
        return yuan.multiply(MULTIPLIER).intValue();
    }

}
