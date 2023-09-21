package com.wind.common.api;


/**
 * 较为安全的 order by field，屏蔽具体的order by 字段
 * 建议使用枚举实现
 *
 * example:
 * <code>
 *     enum GoodsOrderField implements QueryOrderField{
 *
 *         DEFAULT("id"),
 *
 *         PRICE('price'),
 *
 *        SALES('sku.sales'),
 *
 *        private final String orderField;
 *
 *        GoodsOrderField(String orderField){
 *            this.orderField=orderField
 *        }
 *
 *         @Override
 *         String getOrderField(){
 *            return this.orderField;
 *         }
 *     }
 *
 * </code>
 *
 * @author wxup
 */
public interface QueryOrderField {

    /**
     * 获取需要排序的字段
     *
     * @return
     */
    String getOrderField();
}
