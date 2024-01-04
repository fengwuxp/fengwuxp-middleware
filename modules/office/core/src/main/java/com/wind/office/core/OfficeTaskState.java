package com.wind.office.core;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态
 *
 * @author wuxp
 * @date 2023-10-26 17:13
 **/
@AllArgsConstructor
@Getter
public enum OfficeTaskState implements DescriptiveEnum {

    WAIT("待执行"),

    EXECUTING("执行中"),

    COMPLETED("处理成功"),

    FAILED("执行失败"),

    INTERRUPT("执行中断");

    private final String desc;

    public static boolean isFinished(OfficeTaskState state) {
        return state == COMPLETED || state == FAILED || state == INTERRUPT;
    }
}
