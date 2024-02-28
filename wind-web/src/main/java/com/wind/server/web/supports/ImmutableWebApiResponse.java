package com.wind.server.web.supports;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.ExceptionCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.beans.Transient;
import java.io.Serializable;

/**
 * 不可变的 WebApiResponse
 *
 * @author wuxp
 * @date 2024-02-20 10:39
 **/
@ToString
@EqualsAndHashCode
public class ImmutableWebApiResponse<T> implements ApiResp<T>, Serializable {

    private static final long serialVersionUID = -7557721954943132992L;

    /**
     * 响应的http status
     */
    private final transient HttpStatus httpStatus;

    /**
     * 响应数据
     */
    @Getter
    private final T data;

    /**
     * 业务失败时的错误响应码
     */
    private final ExceptionCode errorCode;

    /**
     * 业务失败时的响应消息
     */
    @Getter
    private final String errorMessage;

    @Getter
    private final String traceId;

    public ImmutableWebApiResponse(HttpStatus httpStatus, T data, ExceptionCode errorCode, String errorMessage, String traceId) {
        AssertUtils.notNull(httpStatus, "argument httpStatus must not null");
        AssertUtils.notNull(errorCode, "argument errorCode must not null");
        this.httpStatus = httpStatus;
        this.data = data;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.traceId = traceId;
    }

    @Override
    @Transient
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode.getCode();
    }

    @Override
    public boolean isSuccess() {
        return ExceptionCode.SUCCESSFUL.getCode().equals(errorCode.getCode());
    }

}