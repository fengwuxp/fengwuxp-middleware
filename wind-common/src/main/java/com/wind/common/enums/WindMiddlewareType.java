package com.wind.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.wind.common.WindConstants.WIND_MYSQL_NAME;
import static com.wind.common.WindConstants.WIND_OSS_NAME;
import static com.wind.common.WindConstants.WIND_REDIS_NAME;
import static com.wind.common.WindConstants.WIND_ROCKETMQ_NAME;

/**
 * 中间件类型
 *
 * @author wuxp
 * @date 2023-10-15 12:45
 **/
@AllArgsConstructor
@Getter
public enum WindMiddlewareType implements DescriptiveEnum {

    /**
     * 一般是 Mysql
     */
    MYSQL(WIND_MYSQL_NAME, "数据库"),

    /**
     * redis 缓存
     */
    REDIS(WIND_REDIS_NAME, "REDIS"),

    /**
     * 消息队列
     */
    ROCKETMQ(WIND_ROCKETMQ_NAME, "ROCKETMQ"),

    /**
     * 对象存储
     */
    OSS(WIND_OSS_NAME, "对象存储服务"),

    ;

    private final String configName;

    private final String desc;
}
