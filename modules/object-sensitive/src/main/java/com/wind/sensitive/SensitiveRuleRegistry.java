package com.wind.sensitive;

import com.wind.common.exception.AssertUtils;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuxp
 * @date 2024-03-11 13:30
 **/
public class SensitiveRuleRegistry {

    private final Map<String, SensitiveRuleGroup> ruleGroups;

    public SensitiveRuleRegistry() {
        this(Collections.emptyList());
    }

    public SensitiveRuleRegistry(Collection<SensitiveRuleGroup> ruleGroups) {
        AssertUtils.notNull(ruleGroups, "ruleGroups must not null");
        this.ruleGroups = new ConcurrentHashMap<>(ruleGroups.size());
        ruleGroups.forEach(this::addRule);
    }

    @NotNull
    public SensitiveRuleGroup getRuleGroup(Class<?> target) {
        AssertUtils.notNull(target, "get sensitive rule class not null");
        return ruleGroups.computeIfAbsent(target.getName(), key -> this.buildRuleGroup(target));
    }

    public SensitiveRuleRegistry addRule(SensitiveRuleGroup ruleGroup) {
        this.ruleGroups.put(ruleGroup.getTarget().getName(), ruleGroup);
        return this;
    }

    private SensitiveRuleGroup buildRuleGroup(Class<?> clazz) {
        return SensitiveRuleGroup.builder().form(clazz).last();
    }

}
