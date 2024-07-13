package com.wind.tools.mybatisflex.codegen;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuxp
 * @date 2023-10-06 20:27
 **/
@Getter
@Builder
public class CodegenConfiguration {

    /**
     * 作者
     */
    private final String author = System.getProperty("user.name");

    /**
     * 基础包名
     */
    private final String basePackage;

    /**
     * 输出目录
     */
    private final String outDir;

    /**
     * 控制器请求根路径
     */
    private final String requestBaseMapping;

    public Map<String, Object> getConfigVariables() {
        Map<String, Object> result = new HashMap<>();
        result.put("basePackage", basePackage);
        result.put("author", author);
        result.put("requestBaseMapping", requestBaseMapping);
        return result;
    }
}
