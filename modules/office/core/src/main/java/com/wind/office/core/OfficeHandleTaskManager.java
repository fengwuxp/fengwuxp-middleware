package com.wind.office.core;

import org.springframework.lang.Nullable;

/**
 * 办公文档任务处理管理器
 *
 * @author wuxp
 * @date 2023-10-27 18:09
 **/
public interface OfficeHandleTaskManager {

    /**
     * 创建任务
     *
     * @return 任务实例
     */
    OfficeHandleTask create(OfficeDocumentDescriptor descriptor);

    /**
     * @param taskId 任务 ID
     * @return 任务实例
     */
    @Nullable
    OfficeHandleTask get(String taskId);

    /**
     * 移除任务
     *
     * @param taskId 任务 ID
     * @return 任务实例
     */
    @Nullable
    OfficeHandleTask remove(String taskId);
}
