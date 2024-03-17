package com.wind.sentinel;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.wind.common.exception.AssertUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 流控规则刷新注册器
 *
 * @author wuxp
 * @date 2024-03-12 10:06
 **/
final class SentinelRuleListenRegister {

    /**
     * @key Sentinel 配置类类型
     * @value 注册规则配置监听的函数
     */
    @SuppressWarnings("rawtypes")
    static final Map<Class<?>, Consumer<SentinelProperty>> RULE_LISTEN_REGISTERS = new ConcurrentHashMap<>();

    static {
        RULE_LISTEN_REGISTERS.put(FlowRule.class, FlowRuleManager::register2Property);
        RULE_LISTEN_REGISTERS.put(DegradeRule.class, DegradeRuleManager::register2Property);
        RULE_LISTEN_REGISTERS.put(ParamFlowRule.class, ParamFlowRuleManager::register2Property);
        RULE_LISTEN_REGISTERS.put(SystemRule.class, SystemRuleManager::register2Property);
        RULE_LISTEN_REGISTERS.put(AuthorityRule.class, AuthorityRuleManager::register2Property);
    }

    private SentinelRuleListenRegister() {
        throw new AssertionError();
    }

    @SuppressWarnings("rawtypes")
    static void registerListen(Class<?> clazz, ReadableDataSource dataSource) {
        Consumer<SentinelProperty> consumer = RULE_LISTEN_REGISTERS.get(clazz);
        AssertUtils.notNull(consumer, () -> "unsupported config type: " + clazz.getName());
        consumer.accept(dataSource.getProperty());
    }
}
