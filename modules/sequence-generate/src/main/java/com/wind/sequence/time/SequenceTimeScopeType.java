package com.wind.sequence.time;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2023-10-17 13:52
 **/
@AllArgsConstructor
@Getter
enum SequenceTimeScopeType implements DescriptiveEnum {


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
     * 按天区分
     */
    HOUR("小时", "yyyyMMddHH"),

    /**
     * 按天区分
     */
    MINUTE("分钟", "yyyyMMddHHmm"),

    /**
     * 按天区分
     */
    SECONDS("小时", "yyyyMMddHHmmss");

    private final String desc;

    private final String pattern;
}
