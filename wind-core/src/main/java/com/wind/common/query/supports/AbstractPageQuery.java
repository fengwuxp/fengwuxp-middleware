package com.wind.common.query.supports;


import com.wind.common.exception.AssertUtils;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final AtomicInteger MAX_QUERY_SIZE = new AtomicInteger(5000);

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

    public void setQuerySize(Integer querySize) {
        AssertUtils.isTrue(querySize <= MAX_QUERY_SIZE.get(), () -> String.format("查询大小不能超过：%d", MAX_QUERY_SIZE.get()));
        this.querySize = querySize;
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

    /**
     * 配置查询大小最大值
     *
     * @param querySize 查询大小
     */
    public static void configureMaxQuerySize(int querySize) {
        AssertUtils.isTrue(querySize > 0, "查询大小必须大于 0");
        MAX_QUERY_SIZE.set(querySize);
    }

    /**
     * @return 查询大小最大值
     */
    public int getMaxQuerySize() {
        return MAX_QUERY_SIZE.get();
    }

}
