package com.wind.common.query.supports;


import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询排序类型
 *
 * @author wxup
 */
@AllArgsConstructor
@Getter
public enum QueryOrderType implements DescriptiveEnum {

    DESC("降序"),

    ASC("升序");

    private final String desc;

}
