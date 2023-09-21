package com.wind.common.api;


/**
 * 接口查询请求
 *
 * @author wxup
 */
public interface ApiQueryReq<F extends QueryOrderField> {

    /**
     * @return 当前查询页码
     */
    Integer getQueryPage();

    /**
     * @return 当前查询大小
     */
    Integer getQuerySize();

    /**
     * @return 当前查询类型
     */
    QueryType getQueryType();

    /**
     * @return 获取排序类型
     */
    QueryOrderType[] getOrderTypes();

    /**
     * @return 获取排序字段
     */
    F[] getOrderFields();


    /**
     * 是否需要处理排序
     *
     * @return <code>true</code> 需要处理排序
     */
    default boolean isOrderBy() {
        QueryOrderType[] orderTypes = this.getOrderTypes();
        F[] orderFields = this.getOrderFields();
        if (orderTypes == null || orderFields == null) {
            return false;
        }
        return orderTypes.length == orderFields.length && orderTypes.length > 0;
    }

}
