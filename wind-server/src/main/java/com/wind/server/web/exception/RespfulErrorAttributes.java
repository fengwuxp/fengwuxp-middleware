package com.wind.server.web.exception;

import com.alibaba.fastjson2.JSON;
import com.wind.common.WindConstants;
import com.wind.common.exception.ExceptionCode;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @author wuxp
 * @date 2023-09-26 21:30
 **/
public class RespfulErrorAttributes implements ErrorAttributes {

    public static final ExceptionCode WRAPPER_SPRING_HANDLE_ERROR = new ExceptionCode() {
        @Override
        public String getCode() {
            return "WRAPPER_SPRING_HANDLE_ERROR";
        }

        @Override
        public String getDesc() {
            return "spring 默认处理的错误";
        }
    };

    private final DefaultErrorAttributes attributes;

    public RespfulErrorAttributes(DefaultErrorAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public Throwable getError(WebRequest webRequest) {
        return attributes.getError(webRequest);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> result = attributes.getErrorAttributes(webRequest, options);
        String message = (String) result.remove("message");
        ApiResp<Map<String, Object>> resp = RestfulApiRespFactory.error(result, WRAPPER_SPRING_HANDLE_ERROR, message == null ? WindConstants.UNKNOWN : message);
        return (Map<String, Object>) JSON.toJSON(resp);
    }
}
