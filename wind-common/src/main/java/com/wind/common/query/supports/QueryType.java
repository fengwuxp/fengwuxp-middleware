package com.wind.common.query.supports;


import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wxup
 */
@AllArgsConstructor
@Getter
public enum QueryType implements DescriptiveEnum {


    COUNT_TOTAL("统计总数"),

    QUERY_RESET("查询结果集"),

    QUERY_BOTH("查询总数和结果集");

    private final String desc;

    public boolean isCountTotal() {
        return this.equals(COUNT_TOTAL) || this.equals(QUERY_BOTH);
    }

    public boolean isQueryResult() {
        return this.equals(QUERY_RESET) || this.equals(QUERY_BOTH);
    }
}
