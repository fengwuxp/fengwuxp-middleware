package com.wind.office.core;

import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * 办公文档任务处理信息
 *
 * @author wuxp
 * @date 2024-01-02 18:31
 **/
public interface OfficeDocumentTaskInfo {

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
    @Nullable
    LocalDateTime getBeginTime();

    /**
     * @return 任务结束时间
     */
    @Nullable
    LocalDateTime getEndTime();

    /**
     * @return 获取处理成功的总条数
     */
    int getRowSize();

    /**
     * @return 获取处理失败的总条数
     */
    int getFailedRowSize();

    /**
     * @return 任务是否结束
     */
    default boolean isEnd() {
        return getEndTime() != null;
    }

    /**
     * 更新任务状态
     *
     * @param newState 任务状态
     */
    void updateState(OfficeTaskState newState);

    /**
     * 添加处理成功的行
     *
     * @param row 数据
     */
    void addRow(Object row);

    /**
     * 添加处理失败的行
     *
     * @param row 数据
     */
    void addFailedRow(Object row);
}
