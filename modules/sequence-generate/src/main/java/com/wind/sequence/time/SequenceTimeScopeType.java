package com.wind.sequence.time;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Seq 时间范围类型
 * @author wuxp
 * @date 2023-10-17 13:52
 **/
@AllArgsConstructor
@Getter
public enum SequenceTimeScopeType implements DescriptiveEnum {

    /**
     * 按年区分
     */
    YEAR("年", "yyyy"),

    /**
     * 按月区分
     */
    MONTH("月", "yyyyMM"),

    /**
     * 按天区分
     */
    DAY("天", "yyyyMMdd"),

    /**
     * 按小时区分
     */
    HOUR("小时", "yyyyMMddHH"),

    /**
     * 按分钟区分
     */
    MINUTE("分钟", "yyyyMMddHHmm"),

    /**
     * 按秒区分
     */
    SECONDS("秒", "yyyyMMddHHmmss");

    private final String desc;

    private final String pattern;
}
