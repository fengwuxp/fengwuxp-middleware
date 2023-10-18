package com.wind.sequence.time;

import com.wind.sequence.SequenceGenerator;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 按照时间隔离的序列号生成器
 * 例如：20231029{index}  => 2023102900005
 *
 * @author wuxp
 * @date 2023-10-18 08:02
 **/
public class DateTimeSequenceGenerator implements SequenceGenerator {

    private final SequenceTimeScopeType timeScope;

    private final SequenceGenerator dateCounter;

    private DateTimeSequenceGenerator(SequenceTimeScopeType timeScope, SequenceGenerator dateCounter) {
        this.timeScope = timeScope;
        this.dateCounter = dateCounter;
    }

    @Override
    public String next() {
        return String.format("%s%s", DateFormatUtils.format(new Date(), timeScope.getPattern()), dateCounter.next());
    }

    /**
     * 获取一个以年区分的序列号生成器
     *
     * @param generator 当年用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator year(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.YEAR, generator);
    }

    /**
     * 获取一个以月区分的序列号生成器
     *
     * @param generator 当月用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator month(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.MONTH, generator);
    }

    /**
     * 获取一个以天区分的序列号生成器
     *
     * @param generator 当天用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator day(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.DAY, generator);
    }

    /**
     * 获取一个以小时区分的序列号生成器
     *
     * @param generator 该小时用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator hour(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.HOUR, generator);
    }

    /**
     * 获取一个以分钟区分的序列号生成器
     *
     * @param generator 该分钟用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator minute(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.MINUTE, generator);
    }

    /**
     * 获取一个以秒区分的序列号生成器
     *
     * @param generator 该秒用于生成序列号的生成器
     * @return 序列号生成器
     */
    public static SequenceGenerator seconds(SequenceGenerator generator) {
        return new DateTimeSequenceGenerator(SequenceTimeScopeType.SECONDS, generator);
    }
}
