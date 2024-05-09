package com.wind.tools.mybatisflex.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代码生成输出类型
 *
 * @author wuxp
 * @date 2023-10-07 14:27
 **/
@AllArgsConstructor
@Getter
public enum CodegenOutPutType {

    /**
     * mybatis mapper
     */
    MAPPER("mapper"),

    /**
     * dto 类
     */
    DTO("dto"),

    /**
     * 查询对象
     */
    QUERY("query"),

    /**
     * 创建更新请求对象
     */
    REQUEST("request"),

    /**
     * mapstruct converter
     */
    CONVERTER("mapstruct"),

    /**
     * 服务接口类
     */
    SERVICE(""),

    /**
     * 服务接口实现类
     */
    SERVICE_IMPL("impl"),

    /**
     * 控制器类
     */
    CONTROLLER("controller");

    private final String dir;

}