package com.wind.office.excel.export;

import com.wind.common.WindConstants;
import com.wind.office.core.AbstractDelegateDocumentTask;
import com.wind.office.core.OfficeDocumentTaskInfo;
import com.wind.office.core.OfficeTaskState;
import com.wind.office.excel.ExportExcelDataFetcher;
import com.wind.script.spring.SpringExpressionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.format.Printer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
            for (Object row : rows) {
                addRow(format(row));
            }
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

    private List<String> format(Object row) {
        ExportExcelTaskInfo taskInfo = (ExportExcelTaskInfo) getDelegate();
        List<String> result = new ArrayList<>();
        EvaluationContext context = new StandardEvaluationContext(row);
        for (ExportExcelTaskInfo.ExcelHead excelHead : taskInfo.getHeads()) {
            result.add(formatCellValue(excelHead, context));
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    private String formatCellValue(ExportExcelTaskInfo.ExcelHead excelHead, EvaluationContext context) {
        Object val = SpringExpressionEvaluator.DEFAULT.eval(excelHead.getExpression(), context);
        if (val == null) {
            return WindConstants.EMPTY;
        }
        Printer formatter = excelHead.getFormatter();
        return formatter == null ? String.valueOf(val) : formatter.print(val, Locale.getDefault());
    }

}
