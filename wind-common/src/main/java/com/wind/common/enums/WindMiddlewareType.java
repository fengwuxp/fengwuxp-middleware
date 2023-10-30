package com.wind.common.enums;

import com.wind.common.WindConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.wind.common.WindConstants.WIND_ELASTIC_JOB_NAME;
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
     * wind
     */
    WIND(WindConstants.WIND.toLowerCase(), WindConstants.WIND),

    /**
     * 数据库
     * Mysql
     * https://www.mysql.com/cn/
     */
    MYSQL(WIND_MYSQL_NAME, "数据库"),

    /**
     * redis 缓存
     * https://redis.io/
     * redisson: https://github.com/redisson/redisson
     */
    REDIS(WIND_REDIS_NAME, "REDIS"),

    /**
     * 消息队列
     * rocketmq
     * https://rocketmq.apache.org/zh/
     */
    ROCKETMQ(WIND_ROCKETMQ_NAME, "ROCKETMQ"),

    /**
     * 分布式定时任务调度
     * elasticjob
     * https://shardingsphere.apache.org/elasticjob/
     */
    ELASTIC_JOB(WIND_ELASTIC_JOB_NAME, "ELASTIC-JOB"),

    /**
     * 对象存储
     */
    OSS(WIND_OSS_NAME, "对象存储服务"),

    ;

    private final String configName;

    private final String desc;
}
