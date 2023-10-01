package com.wind.payment.core.util;

import java.math.BigDecimal;

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
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fen2Yun(Integer fen) {
        return new BigDecimal(fen).divide(MULTIPLIER).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 元转为分
     *
     * @param yuan 元
     * @return 分
     */
    public static int yuanToFen(String yuan) {
        return yuanToFen(new BigDecimal(yuan));
    }

    /**
     * 元转为分
     *
     * @param yuan 元
     * @return 分
     */
    public static int yuanToFen(BigDecimal yuan) {
        return yuan.multiply(MULTIPLIER).intValue();
    }

}
