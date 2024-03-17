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

    private static final QueryOrderType[] DESC_TYPES = of(QueryOrderType.DESC);

    private static final QueryOrderType[] ASC_TYPES = of(QueryOrderType.ASC);

    /**
     * @return 降序排序
     */
    public static QueryOrderType[] desc() {
        return DESC_TYPES;
    }

    /**
     * @return 升序排序
     */
    public static QueryOrderType[] asc() {
        return ASC_TYPES;
    }

    /**
     * 工厂方法方便用户传参
     *
     * @param types 排序类型列表
     * @return 排序类型列表
     */
    public static QueryOrderType[] of(QueryOrderType... types) {
        return types;
    }
}
