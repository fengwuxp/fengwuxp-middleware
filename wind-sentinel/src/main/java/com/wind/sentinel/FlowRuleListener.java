package com.wind.sentinel;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.List;

/**
 * sentinel 流控规则配置监听器
 * {@link com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager}
 *
 * @author wuxp
 * @date 2024-02-22 18:53
 **/
public interface FlowRuleListener {

    /**
     * 流量规则发生改变
     *
     * @param rules 配置规则
     */
    void onChange(List<FlowRule> rules);

}
