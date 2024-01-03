package com.wind.office.excel.export;

import com.wind.office.core.OfficeDocumentTaskInfo;
import com.wind.office.core.OfficeTaskState;
import com.wind.office.excel.ExcelDocumentWriter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.format.Formatter;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * excel 文档任务 Info
 *
 * @author wuxp
 * @date 2023-10-27 18:31
 **/
@Getter
@Builder
public class ExportExcelTaskInfo implements OfficeDocumentTaskInfo {

    private final String id;

    private final String name;

    private final AtomicReference<OfficeTaskState> state;

    private final AtomicReference<LocalDateTime> beginTime;

    private final AtomicReference<LocalDateTime> endTime;

    private final AtomicInteger rowSize = new AtomicInteger(0);

    private final AtomicInteger failedRowSize = new AtomicInteger(0);

    /**
     * 导出 excel 列头配置
     */
    private final List<ExcelHead> heads;

    /**
     * 一次抓取数据的大小
     */
    private final int fetchSize;

    private final ExcelDocumentWriter writer;

    @Transient
    public List<ExcelHead> getHeads() {
        return heads;
    }

    @Transient
    public ExcelDocumentWriter getWriter() {
        return writer;
    }

    @Override
    public OfficeTaskState getState() {
        return state.get();
    }

    @Override
    public int getRowSize() {
        return rowSize.get();
    }

    @Override
    public int getFailedRowSize() {
        return failedRowSize.get();
    }

    @Override
    public LocalDateTime getBeginTime() {
        return beginTime.get();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime.get();
    }

    @Override
    public void addRow(Object row) {
        writer.write(row);
        rowSize.incrementAndGet();
    }

    @Override
    public void addFailedRow(Object row) {
        writer.write(row);
        failedRowSize.incrementAndGet();
    }

    @Override
    public void updateState(OfficeTaskState newState) {
        if (Objects.equals(newState, OfficeTaskState.EXECUTING)) {
            this.beginTime.set(LocalDateTime.now());
        }
        if (OfficeTaskState.isFinished(newState)) {
            if (Objects.equals(newState, OfficeTaskState.COMPLETED)) {
                writer.finish();
            }
            this.endTime.set(LocalDateTime.now());
        }
        this.state.set(newState);
    }

    @AllArgsConstructor
    @Getter
    public static class ExcelHead {

        /**
         * 列标题
         */
        private final String title;

        /**
         * 取值表达式
         * 默认使用 spring expression 表达式
         * {@link org.springframework.expression.spel.standard.SpelExpressionParser}
         */
        private final String expression;

        /**
         * 列转换器
         */
        private final Formatter<?> formatter;
    }

    public static ExportExcelTaskInfo of(String name, List<ExcelHead> heads, ExcelDocumentWriter writer) {
        return ExportExcelTaskInfo.builder()
                .id(RandomStringUtils.randomAlphanumeric(32))
                .name(name)
                .beginTime(new AtomicReference<>())
                .endTime(new AtomicReference<>())
                .state(new AtomicReference<>(OfficeTaskState.WAIT))
                .fetchSize(500)
                .heads(heads)
                .writer(writer)
                .build();
    }

}
