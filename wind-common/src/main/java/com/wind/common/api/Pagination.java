package com.wind.common.api;


import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @author wuxp
 */
public interface Pagination<T> extends Serializable {

    /**
     * @return 总记录数据
     */
    long getTotal();

    /**
     * @return 数据集合列表
     */
    List<T> getRecords();

    /**
     * @return 当前查询页面
     */
    int getQueryPage();

    /**
     * @return 当前查询大小
     */
    int getQuerySize();

    /**
     * @return 当前查询类型
     */
    QueryType getQueryType();

    /**
     * 为了节省传输内容，该方法不参与序列化
     *
     * @return 获取第一条数据
     */
    @Transient
    T getFirst();

    /**
     * @return {@link #getRecords()}是否为null 或空集合
     */
    @Transient
    boolean isEmpty();

}
