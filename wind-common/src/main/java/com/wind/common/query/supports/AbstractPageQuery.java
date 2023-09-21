package com.wind.common.query.supports;


import com.wind.common.exception.AssertUtils;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分页查询对象基类
 *
 * @author wxup
 */
@Data
public abstract class AbstractPageQuery<F extends QueryOrderField> {

    /**
     * 避免查询页面数据过大，拖垮数据库
     */
    public static final int MAX_QUERY_SIZE = 5000;

    /**
     * 当前查询页码
     */
    @NotNull
    private Integer queryPage = 1;

    /**
     * 当前查询大小
     */
    @NotNull
    private Integer querySize = 20;

    /**
     * 当前查询类型
     */
    private QueryType queryType = QueryType.QUERY_BOTH;

    /**
     * 获取排序字段
     * 排序字段和排序类型安装数组顺序一一对应
     */
    private F[] orderFields;

    /**
     * 获取排序类型
     */
    private QueryOrderType[] orderTypes;


    public Integer getQuerySize() {
        AssertUtils.isTrue(queryPage <= MAX_QUERY_SIZE, String.format("查询大小不能超过：%d", MAX_QUERY_SIZE));
        return querySize;
    }

    /**
     * 是否需要处理排序
     *
     * @return <code>true</code> 需要处理排序
     */
    public boolean requireOrderBy() {
        if (orderFields == null || orderTypes == null) {
            return false;
        }
        return orderFields.length > 0 && orderFields.length == orderTypes.length;
    }

}
