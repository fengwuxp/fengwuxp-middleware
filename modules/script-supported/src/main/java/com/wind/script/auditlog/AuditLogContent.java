package com.wind.script.auditlog;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author wuxp
 * @date 2023-09-23 09:43
 **/
@Getter
@Builder
public class AuditLogContent {

    /**
     * 日志内容
     */
    private final String log;

    /**
     * 操作日志业务类型
     */
    private final String type;


    /**
     * 操作类型，比如：CREATE
     */
    private final String operation;

    /**
     * 请求参数
     */
    private final Object[] args;

    /**
     * 方法方法返回值
     */
    @Nullable
    private final Object resultValue;

    /**
     * 日志记录时的上下文变量
     * 例如：当前操作用户、请求 ip 等
     */
    private final Map<String, Object> contextVariables;

    /**
     * 审计日志操作分组
     */
    private final String group;

    /**
     * 审计操作资源类型，可以是操作对象的类名 OR 表名
     */
    private final String resourceType;

    /**
     * 操作的目标资源标识
     */
    @Nullable
    private final Object resourceId;

    /**
     * 异常
     */
    @Nullable
    private final Throwable throwable;

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key) {
        return (T) contextVariables.get(key);
    }

}
