package com.wind.common.exception;

import com.wind.api.core.ApiResponse;
import lombok.Getter;

/**
 * Api Client Exception
 *
 * @author wuxp
 * @date 2024-02-28 17:34
 **/
@Getter
public class ApiClientException extends BaseException {

    private final ApiResponse<?> response;

    public ApiClientException(ApiResponse<?> response, String message) {
        super(message);
        this.response = response;
    }

    public String getTraceId() {
        return response.getTraceId();
    }

    public String getErrorCode() {
        return response.getErrorCode();
    }
}
