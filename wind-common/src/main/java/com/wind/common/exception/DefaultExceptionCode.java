package com.wind.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 默认提供的通用异常
 *
 * @author wuxp
 * @date 2023-09-22 11:31
 **/
@Getter
@AllArgsConstructor
public enum DefaultExceptionCode implements ExceptionCode {


    BAD_REQUEST("400", "请求不合法"),

    UNAUTHORIZED("401", "未认证"),

    FORBIDDEN("403", "无权限"),

    NOT_FOUND("404", "资源不存在"),

    COMMON_ERROR("500", "通用业务错误");

    private final String code;

    private final String desc;

}
