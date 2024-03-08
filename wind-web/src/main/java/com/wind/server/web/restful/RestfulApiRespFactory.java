package com.wind.server.web.restful;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.exception.ExceptionCode;
import com.wind.common.query.supports.Pagination;
import com.wind.server.web.supports.ApiResp;
import com.wind.server.web.supports.ImmutableWebApiResponse;
import com.wind.trace.WindTracer;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;


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

    public static <T> ApiResp<T> ok() {
        return ok(null);
    }

    public static <T> ApiResp<T> ok(T data) {
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
        return ok(pagination);
    }


    /*-------------------- 4xx -------------------*/

    public static <T> ApiResp<T> badRequest() {
        return badRequest(DefaultExceptionCode.BAD_REQUEST.getCode());
    }

    public static <T> ApiResp<T> badRequest(String errorMessage) {
        return of(HttpStatus.BAD_REQUEST, null, DefaultExceptionCode.BAD_REQUEST, errorMessage);
    }

    public static <T> ApiResp<T> notFound() {
        return notFound(DefaultExceptionCode.NOT_FOUND.getDesc());
    }

    public static <T> ApiResp<T> notFound(String errorMessage) {
        return of(HttpStatus.NOT_FOUND, null, DefaultExceptionCode.NOT_FOUND, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized() {
        return unAuthorized("未登录，请先登录");
    }


    public static <T> ApiResp<T> unAuthorized(String errorMessage) {
        return unAuthorized(DefaultExceptionCode.UNAUTHORIZED, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized(ExceptionCode code, String errorMessage) {
        return of(HttpStatus.UNAUTHORIZED, null, code, errorMessage);
    }

    public static <T> ApiResp<T> unAuthorized(T data, String errorMessage) {
        return of(HttpStatus.UNAUTHORIZED, data, DefaultExceptionCode.UNAUTHORIZED, errorMessage);
    }


    public static <T> ApiResp<T> forBidden() {
        return forBidden("无权限访问该资源");
    }

    public static <T> ApiResp<T> forBidden(String errorMessage) {
        return forBidden(null, errorMessage);
    }

    public static <T> ApiResp<T> forBidden(T data, String errorMessage) {
        return of(HttpStatus.FORBIDDEN, data, DefaultExceptionCode.FORBIDDEN, errorMessage);
    }

    public static <T> ApiResp<T> toManyRequests() {
        return of(HttpStatus.TOO_MANY_REQUESTS, null, DefaultExceptionCode.TO_MANY_REQUESTS, DefaultExceptionCode.TO_MANY_REQUESTS.getDesc());
    }

    public static <T> ApiResp<T> toManyRequests(String errorMessage) {
        return of(HttpStatus.TOO_MANY_REQUESTS, null, DefaultExceptionCode.TO_MANY_REQUESTS, errorMessage);
    }


    /*-------------------- business handle error 5xx -------------------*/

    public static <T> ApiResp<T> error() {
        return error("请求处理出现错误");
    }

    public static <T> ApiResp<T> error(String errorMessage) {
        return error(DefaultExceptionCode.COMMON_ERROR, errorMessage);
    }

    public static <T> ApiResp<T> error(ExceptionCode code, String errorMessage) {
        return error(null, code, errorMessage);
    }

    public static <T> ApiResp<T> error(T data, ExceptionCode code, String errorMessage) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, data, code, errorMessage);
    }

    public static <T> ApiResp<T> withThrowable(Throwable throwable) {
        String errorMessage = StringUtils.hasText(throwable.getMessage()) ? throwable.getMessage() : "unknown error";
        if (throwable instanceof BaseException) {
            ExceptionCode code = ((BaseException) throwable).getCode();
            if (code instanceof DefaultExceptionCode) {
                HttpStatus status = HttpStatus.resolve(Integer.parseInt(code.getCode()));
                return of(status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status, null, code, errorMessage);
            }
            return error(code, errorMessage);
        }
        return error(errorMessage);
    }

    private static <T> ApiResp<T> of(HttpStatus httpStatus, T data, ExceptionCode code, String errorMessage) {
        return new ImmutableWebApiResponse<>(httpStatus, data, code, errorMessage, WindTracer.TRACER.getTraceId());
    }

}
