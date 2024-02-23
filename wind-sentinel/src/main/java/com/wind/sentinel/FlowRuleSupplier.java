package com.wind.sentinel;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.List;
import java.util.function.Supplier;

/**
 * sentinel 流量规则配置提供者
 *
 * @author wuxp
 * @date 2024-02-22 18:53
 **/
public interface FlowRuleSupplier extends Supplier<List<FlowRule>> {


}
