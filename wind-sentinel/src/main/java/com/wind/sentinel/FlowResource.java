package com.wind.sentinel;

import com.alibaba.csp.sentinel.EntryType;

/**
 * 流控资源定义
 *
 * @author wuxp
 * @date 2024-03-07 15:24
 **/
public interface FlowResource {

    /**
     * @return 资源名称
     */
    String getName();

    /**
     * @return 流量类型
     */
    EntryType getEntryType();

    /**
     * @return 资源类型
     */
    int getResourceType();

    /**
     * @return 请求上下文名称
     */
    String getContextName();

    /**
     * @return 请求来源
     */
    String getOrigin();
}
