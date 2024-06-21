package com.wind.common.spring.event;

/**
 * 在事务中多次发送同一事件，只会发送一次
 * {@link com.wind.common.spring.SpringEventPublishUtils#publishEventIfInTransaction(Object)}
 *
 * @author wuxp
 * @date 2024-06-21 10:10
 **/
public interface SpringTransactionEvent {

    /**
     * @return 事件标识，需要保证全局唯一
     */
    String getEventId();
}
