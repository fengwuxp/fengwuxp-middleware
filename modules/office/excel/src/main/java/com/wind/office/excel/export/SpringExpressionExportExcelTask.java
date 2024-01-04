package com.wind.office.excel.export;

import com.wind.office.core.AbstractDelegateDocumentTask;
import com.wind.office.core.OfficeTaskState;
import com.wind.office.excel.ExportExcelDataFetcher;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 基于 spring expression 取值 excel 导出任务，不支持列合并等操作
 *
 * @author wuxp
 * @date 2023-10-27 18:28
 **/
@Slf4j
public class SpringExpressionExportExcelTask extends AbstractDelegateDocumentTask {

    private final ExportExcelDataFetcher<?> fetcher;

    public SpringExpressionExportExcelTask(ExportExcelTaskInfo taskInfo, ExportExcelDataFetcher<?> fetcher) {
        super(taskInfo);
        this.fetcher = fetcher;
    }

    @Override
    protected void doTask() {
        int queryPage = 1;
        ExportExcelTaskInfo taskInfo = (ExportExcelTaskInfo) getDelegate();
        while (true) {
            List<?> rows = fetcher.fetch(queryPage, taskInfo.getFetchSize());
            rows.forEach(this::addRow);
            if (Objects.equals(OfficeTaskState.INTERRUPT, getState())) {
                log.info("excel task is interrupted，id = {}", getId());
                updateState(OfficeTaskState.INTERRUPT);
                return;
            }
            if (rows.size() < taskInfo.getFetchSize()) {
                // 处理完成
                break;
            }
            queryPage++;
        }
    }

}
