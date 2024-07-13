package com.wind.common.enums;

import com.wind.common.WindConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.wind.common.WindConstants.WIND_DYNAMIC_TP;
import static com.wind.common.WindConstants.WIND_ELASTIC_JOB_NAME;
import static com.wind.common.WindConstants.WIND_MYSQL_NAME;
import static com.wind.common.WindConstants.WIND_OSS_NAME;
import static com.wind.common.WindConstants.WIND_REDIS_NAME;
import static com.wind.common.WindConstants.WIND_ROCKETMQ_NAME;
import static com.wind.common.WindConstants.WIND_SENTINEL_NAME;

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
     * sentinel
     * https://github.com/alibaba/Sentinel
     */
    SENTINEL(WIND_SENTINEL_NAME, "SENTINEL"),

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

    /**
     * 线程池
     * https://github.com/dromara/dynamic-tp
     */
    DYNAMIC_TP(WIND_DYNAMIC_TP, "动态线程池"),

    ;

    /**
     * 配置在配置文件中的 key 名称
     */
    private final String configName;

    /**
     * 描述
     */
    private final String desc;
}
