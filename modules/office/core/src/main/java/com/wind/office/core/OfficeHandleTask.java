package com.wind.office.core;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * 办公文档处理任务
 *
 * @author wuxp
 * @date 2023-10-26 17:05
 **/
public interface OfficeHandleTask {

    /**
     * @return 任务 ID
     */
    String getId();

    /**
     * @return 任务名称
     */
    String getName();

    /**
     * @return 任务状态
     */
    OfficeTaskState getState();

    /**
     * @return 任务开始时间
     */
    LocalDateTime getBeginTime();

    /**
     * @return 任务结束时间
     */
    @Nullable
    LocalDateTime getEndTime();

    /**
     * @return 任务是否结束
     */
    default boolean isEnd() {
        return getEndTime() != null;
    }

    /**
     * 处理任务
     */
    void handle();
}
