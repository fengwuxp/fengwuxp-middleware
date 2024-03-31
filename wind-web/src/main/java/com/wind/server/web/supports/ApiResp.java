package com.wind.server.web.supports;

import com.wind.api.core.ApiResponse;
import org.springframework.http.HttpStatus;

import java.beans.Transient;

/**

 * api 统一响应
 * TODO rename WebApiResponse
 *
 * @param <T>
 * @author wuxp
 */
public interface ApiResp<T> extends ApiResponse<T> {

    /**
     * 获取本次请求响应的 http状态码
     *
     * @return {@link HttpStatus} <code>{@link HttpStatus#OK}</code> 通讯层面表示成功
     */
    @Transient
    HttpStatus getHttpStatus();
}
