package com.wind.mask;

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
public class ObjectMaskRuleRegistry {

    private final Map<String, MaskRuleGroup> ruleGroups;

    public ObjectMaskRuleRegistry() {
        this(Collections.emptyList());
    }

    public ObjectMaskRuleRegistry(Collection<MaskRuleGroup> ruleGroups) {
        AssertUtils.notNull(ruleGroups, "ruleGroups must not null");
        this.ruleGroups = new ConcurrentHashMap<>(ruleGroups.size());
        ruleGroups.forEach(this::addRule);
    }

    @NotNull
    public MaskRuleGroup getRuleGroup(Class<?> target) {
        AssertUtils.notNull(target, "get sensitive rule class not null");
        return ruleGroups.computeIfAbsent(target.getName(), key -> this.buildRuleGroup(target));
    }

    public ObjectMaskRuleRegistry addRule(MaskRuleGroup ruleGroup) {
        this.ruleGroups.put(ruleGroup.getTarget().getName(), ruleGroup);
        return this;
    }

    private MaskRuleGroup buildRuleGroup(Class<?> clazz) {
        return MaskRuleGroup.builder().form(clazz).last();
    }

}
