package com.wind.sentinel;

import com.wind.common.enums.DescriptiveEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2024-06-18 10:01
 **/
@AllArgsConstructor
@Getter
public enum SentinelResourcesType implements DescriptiveEnum {

    HTTP_API(0, "http.api", "http 接口"),

    ROCKETMQ_CONSUMER(61, "rocketmq.consumer", "RocketMq consumer");

    private final int code;

    private final String typeName;

    private final String desc;
}
