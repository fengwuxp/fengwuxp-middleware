package com.wind.api.core;

import com.wind.common.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 不可变的 ApiResponse
 *
 * @author wuxp
 * @date 2024-02-28 11:19
 **/
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ImmutableApiResponse<T> implements ApiResponse<T>, Serializable {

    /**
     * 响应数据
     */
    @Getter
    private final T data;

    /**
     * 业务失败时的错误响应码
     */
    private final String errorCode;

    /**
     * 业务失败时的响应消息
     */
    @Getter
    private final String errorMessage;

    @Getter
    private final String traceId;

    @Override
    public boolean isSuccess() {
        return ExceptionCode.SUCCESSFUL.getCode().equals(errorCode);
    }

    // 给序列化框架使用
    ImmutableApiResponse() {
        this(null, null, null, null);
    }
}
