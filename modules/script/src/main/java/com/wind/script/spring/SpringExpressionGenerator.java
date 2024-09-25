package com.wind.script.spring;


import com.wind.common.WindConstants;
import com.wind.script.expression.ExpressionDescriptor;
import com.wind.script.expression.LogicalOp;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * spring expression 表达式生成器
 * 官方文档： https://docs.spring.io/spring-framework/reference/core/expressions.html
 * 参考文档： https://blueblue233.github.io/blog/84ca0064/
 *
 * @author wuxp
 * @version SpringExpressionGenerator.java, v 0.1 2022年08月16日 11:20 wuxp
 * @see SpringExpressionGenerator
 */
public final class SpringExpressionGenerator {

    private static final SpringExpressionConditionalExpressionJoiner JOINER = new SpringExpressionConditionalExpressionJoiner();

    private SpringExpressionGenerator() {
        throw new AssertionError();
    }

    /**
     * 根据ConditionalNode生成对应的spel表达式
     *
     * @param node 节点 不支持为空
     * @return 表达式
     */
    public static String generate(ExpressionDescriptor node) {
        String parentCode = JOINER.join(node.getLeft(), node.getRight(), node.getOp());
        List<ExpressionDescriptor> children = node.getChildren();
        String childrenCode = getRight(children, node.getConjunctions());
        if (StringUtils.hasText(parentCode) && !StringUtils.hasText(childrenCode)) {
            return parentCode;
        }
        if (!StringUtils.hasText(parentCode) && StringUtils.hasText(childrenCode)) {
            return childrenCode;
        }
        if (children != null && children.size() > 1) {
            return String.format("%s %s (%s)", parentCode, node.getConjunctions(), childrenCode);
        }
        return String.format("%s %s %s", parentCode, node.getConjunctions(), childrenCode);
    }

    /**
     * 根据 {@link ExpressionDescriptor}生成对应的 Spel 表达式
     *
     * @param node 节点，支持为空
     * @return Spel 表达式
     */
    public static String generateNullAllowed(ExpressionDescriptor node) {
        if (node == null) {
            return WindConstants.EMPTY;
        }

        return generate(node);
    }

    private static String getRight(List<ExpressionDescriptor> children, LogicalOp relation) {
        if (ObjectUtils.isEmpty(children)) {
            return WindConstants.EMPTY;
        }

        return children.stream()
                .map(node -> {
                    String code = SpringExpressionGenerator.generate(node);
                    if (ObjectUtils.isEmpty(node.getChildren())) {
                        return code;
                    }
                    if (children.size() > 1) {
                        // 有 children 需要用 () 包起来
                        return String.format("(%s)", code);
                    }
                    return String.format("%s", code);
                })
                .collect(Collectors.joining(String.format(" %s ", relation)));
    }

}