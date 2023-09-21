package com.wind.common.query.supports;


/**
 * 较为安全的 order by field，屏蔽具体的order by 字段
 * 建议使用枚举实现
 * <p>
 * example:
 * <code>
 * enum GoodsOrderField implements QueryOrderField{
 * <p>
 * DEFAULT("id"),
 * <p>
 * PRICE('price'),
 * <p>
 * SALES('sku.sales'),
 * <p>
 * private final String orderField;
 * <p>
 * GoodsOrderField(String orderField){
 * this.orderField=orderField
 * }
 *
 * @author wxup
 * @Override String getOrderField(){
 * return this.orderField;
 * }
 * }
 *
 * </code>
 */
public interface QueryOrderField {

    /**
     * @return 需要排序的字段名
     */
    String getOrderField();
}
