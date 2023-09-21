package com.wind.server.web.restful;

import com.wind.common.query.supports.Pagination;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.ExceptionCode;
import com.wind.server.web.supports.ApiResp;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.beans.Transient;
import java.io.Serializable;


/**
 * 统一响应工厂
 *
 * @author wuxp
 */
public final class RestfulApiRespFactory {

    private RestfulApiRespFactory() {
        throw new AssertionError();
    }


    /*-------------------- 2xx -------------------*/

    public static <T> ApiResp<T> successful() {
        return successful(null);
    }

    public static <T> ApiResp<T> successful(T data) {
        return of(HttpStatus.OK, data, ExceptionCode.SUCCESSFUL, null);
    }

    /**
     * 返回查询成功
     *
     * @param pagination 分页信息
     * @param <T>        分页内容类型
     * @return ApiResp<T>
     */
    public static <T> ApiResp<Pagination<T>> pagination(Pagination<T> pagination) {
        return successful(pagination);
    }


    /*-------------------- 4xx -------------------*/

    public static <T> ApiResp<T> badRequest() {
        return badRequest("请求参数有误");
    }

    public static <T> ApiResp<T> badRequest(String errorMessage) {
        return of(HttpStatus.BAD_REQUEST, null, ExceptionCode.DEFAULT_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> notFound() {
        return notFound("请求目标未找到");
    }

    public static <T> ApiResp<T> notFound(String errorMessage) {
        return of(HttpStatus.NOT_FOUND, null, ExceptionCode.DEFAULT_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized(String errorMessage) {
        return unAuthorized(ExceptionCode.DEFAULT_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized(ExceptionCode code, String errorMessage) {
        return unAuthorized(null, code, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized(T data, ExceptionCode code, String errorMessage) {
        return of(HttpStatus.UNAUTHORIZED, data, code, errorMessage);
    }

    public static <T> ApiResp<T> forBidden() {
        return forBidden(ExceptionCode.DEFAULT_ERROR, "无权限访问该资源");
    }

    public static <T> ApiResp<T> forBidden(String errorMessage) {
        return forBidden(ExceptionCode.DEFAULT_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> forBidden(ExceptionCode code, String errorMessage) {
        return forBidden(null, code, errorMessage);
    }

    public static <T> ApiResp<T> forBidden(T data, ExceptionCode code, String errorMessage) {
        return of(HttpStatus.FORBIDDEN, data, code, errorMessage);
    }


    /*-------------------- business handle error -------------------*/

    public static <T> ApiResp<T> error() {
        return forBidden(ExceptionCode.DEFAULT_ERROR, "请求处理出现错误");
    }

    public static <T> ApiResp<T> error(String errorMessage) {
        return forBidden(ExceptionCode.DEFAULT_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> error(ExceptionCode code, String errorMessage) {
        return forBidden(null, code, errorMessage);
    }

    public static <T> ApiResp<T> error(T data, ExceptionCode code, String errorMessage) {
        return of(HttpStatus.FORBIDDEN, data, code, errorMessage);
    }

    private static <T> ApiResp<T> of(HttpStatus httpStatus, T data, ExceptionCode code, String errorMessage) {
        return new DefaultRestfulApiRespImpl<>(httpStatus, data, code, errorMessage);
    }


    static final class DefaultRestfulApiRespImpl<T> implements ApiResp<T>, Serializable {

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

        DefaultRestfulApiRespImpl(HttpStatus httpStatus, T data, ExceptionCode errorCode, String errorMessage) {
            AssertUtils.notNull(httpStatus, "argument httpStatus must not null");
            AssertUtils.notNull(errorCode, "argument errorCode must not null");
            this.httpStatus = httpStatus;
            this.data = data;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
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

}
