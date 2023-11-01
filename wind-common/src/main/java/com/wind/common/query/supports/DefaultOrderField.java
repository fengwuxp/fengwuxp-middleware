package com.wind.common.query.supports;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 默认查询排序字段
 *
 * @author wuxp
 * @since 2023-10-21
 */
@AllArgsConstructor
@Getter
public enum DefaultOrderField implements QueryOrderField {

    /**
     * 创建日期
     */
    GMT_CREATE("gmt_create"),

    /**
     * 编辑日期
     */
    GMT_MODIFIED("gmt_modified");

    /**
     * 排序字段
     */
    private final String orderField;

}
