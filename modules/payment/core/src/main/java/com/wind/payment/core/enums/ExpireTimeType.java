package com.wind.payment.core.enums;

import com.wind.common.WindConstants;
import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 过期时间类型
 *
 * @author wuxp
 * @date 2023-10-01 09:55
 **/
@AllArgsConstructor
@Getter
public enum ExpireTimeType implements DescriptiveEnum {

    MINUTE("m", "分钟"),

    HOUR_OF_DAY("h", "小时"),

    DAY_OF_YEAR("d", "天"),

    CURRENT("c", "当天");

    private final String symbol;

    private final String desc;

    /**
     * 获取ali rule的时间描述
     *
     * @param num 过期时间
     * @return 过期规则描述
     */
    public String getAliRuleDesc(int num) {
        return String.format("%s%s", num, this.getSymbol());
    }

    /**
     * 获取支付过期时间
     * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）
     *
     * @param expr 时区时间表达式
     * @return 过期时间
     */
    public static Date parseExpireTime(String expr) {
        TimeZone zone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(zone);
        if (expr.endsWith(ExpireTimeType.MINUTE.symbol)) {
            String m = expr.replace(ExpireTimeType.MINUTE.symbol, WindConstants.EMPTY);
            calendar.add(Calendar.MINUTE, Integer.parseInt(m));
        } else if (expr.endsWith(ExpireTimeType.HOUR_OF_DAY.symbol)) {
            String m = expr.replace(ExpireTimeType.HOUR_OF_DAY.symbol, WindConstants.EMPTY);
            calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(m));

        } else if (expr.endsWith(ExpireTimeType.DAY_OF_YEAR.symbol)) {
            String m = expr.replace(ExpireTimeType.DAY_OF_YEAR.symbol, WindConstants.EMPTY);
            calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(m));
        } else if (expr.endsWith(ExpireTimeType.CURRENT.symbol)) {
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }
        return calendar.getTime();
    }

}
