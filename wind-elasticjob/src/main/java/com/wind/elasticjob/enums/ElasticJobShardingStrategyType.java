package com.wind.elasticjob.enums;

/**
 * 作业分片策略
 * https://shardingsphere.apache.org/elasticjob/current/cn/user-manual/configuration/built-in-strategy/sharding/
 *
 * @author wuxp
 * @date 2023-11-01 10:49
 **/
public enum ElasticJobShardingStrategyType {

    /**
     * 平均分片策略
     * 如果作业服务器数量与分片总数无法整除，多余的分片将会顺序的分配至每一个作业服务器。
     */
    AVG_ALLOCATION,

    /**
     * 奇偶分片策略
     * 根据作业名称哈希值的奇偶数决定按照作业服务器 IP 升序或是降序的方式分片。
     * 如果作业名称哈希值是偶数，则按照 IP 地址进行升序分片； 如果作业名称哈希值是奇数，则按照 IP 地址进行降序分片。 可用于让服务器负载在多个作业共同运行时分配的更加均匀。
     */
    ODEVITY,

    /**
     * 轮询分片策略
     * 根据作业名称轮询分片。
     */
    ROUND_ROBIN;


}
