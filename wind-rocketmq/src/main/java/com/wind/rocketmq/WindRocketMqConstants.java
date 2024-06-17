package com.wind.rocketmq;

/**
 * @author wuxp
 * @date 2024-06-17 17:02
 **/
public final class WindRocketMqConstants {

    private WindRocketMqConstants() {
        throw new AssertionError();
    }

    /**
     * 默认消息最大重试次数
     */
    public static final int DEFAULT_MAX_RECONSUME_TIMES = 16;
}
