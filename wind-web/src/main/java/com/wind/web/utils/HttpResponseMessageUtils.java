package com.wind.web.utils;

import com.alibaba.fastjson.JSON;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 用于写回 http 响应
 *
 * @author wuxp
 * @date 2023-09-28 09:15
 **/
public final class HttpResponseMessageUtils {

    private HttpResponseMessageUtils() {
        throw new AssertionError();
    }

    /**
     * 响应返回 json 数据
     *
     * @param response http response
     * @param data     响应数据
     */
    public static void writeJson(HttpServletResponse response, Object data) {
        writeJsonText(response, JSON.toJSONString(data));
    }

    /**
     * 响应返回 json数据
     * 注意该方法调用后会关闭响应流
     *
     * @param response http response
     * @param data     响应数据
     */
    public static void writeJsonText(HttpServletResponse response, String data) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 中文乱码处理
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "response write error", e);
        }
    }
}
