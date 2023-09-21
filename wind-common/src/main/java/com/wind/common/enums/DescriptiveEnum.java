package com.wind.common.enums;


/**
 * 带描述的枚举，实现该接口的枚举都是可描述的
 *
 * @author wxup
 **/
public interface DescriptiveEnum {


    /**
     * 枚举的名称
     *
     * @return
     */
    default String name() {
        return "";
    }

    /**
     * 枚举描述中文说明
     *
     * @return
     */
    String getDesc();

    /**
     * 获取英文的枚举描述说明
     *
     * @return
     */
    default String getEnDesc() {
        return "";
    }
}
