package com.wind.office.excel.export;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.wind.common.WindConstants;
import com.wind.office.excel.ExcelDocumentWriter;
import com.wind.script.spring.SpringExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.format.Printer;

import javax.annotation.Nullable;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 基于 easyexcel 的 excel writer
 *
 * @author wuxp
 * @date 2024-01-03 13:19
 * @github https://github.com/alibaba/easyexcel
 **/
public class DefaultEasyExcelDocumentWriter implements ExcelDocumentWriter {

    private final List<Object> rows;

    private final List<ExcelWriteHead> heads;

    private final ExcelWriterSheetBuilder sheetBuilder;

    private DefaultEasyExcelDocumentWriter(List<ExcelWriteHead> heads, ExcelWriterSheetBuilder sheetBuilder, int size) {
        this.rows = new ArrayList<>(size);
        this.heads = heads;
        this.sheetBuilder = sheetBuilder;
    }

    public static DefaultEasyExcelDocumentWriter simple(OutputStream output, List<ExcelWriteHead> heads) {
        List<WriteHandler> handlers = Arrays.asList(
                new SimpleColumnWidthStyleStrategy(25),
                new SimpleRowHeightStyleStrategy((short) 25, (short) 25));
        return of(output, heads, handlers);
    }

    public static DefaultEasyExcelDocumentWriter of(OutputStream output, List<ExcelWriteHead> heads, Collection<WriteHandler> handlers) {
        List<String> titles = heads.stream().map(ExcelWriteHead::getTitle).collect(Collectors.toList());
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.file(output)
                .head(titles.stream().map(Collections::singletonList).collect(Collectors.toList()))
                .needHead(true)
                .charset(StandardCharsets.UTF_8);
        for (WriteHandler handler : handlers) {
            builder.registerWriteHandler(handler);
        }
        return new DefaultEasyExcelDocumentWriter(heads, builder.sheet(), 1000);
    }

    @Override
    public void write(Object row) {
        rows.add(format(row));
    }

    @Override
    public void write(Collection<Object> rows) {
        rows.forEach(this::write);
    }

    @Override
    public void finish() {
        sheetBuilder.doWrite(rows);
        rows.clear();
    }

    private List<String> format(Object row) {
        List<String> result = new ArrayList<>();
        EvaluationContext context = new StandardEvaluationContext(row);
        for (ExcelWriteHead writeHead : heads) {
            Object val = SpringExpressionEvaluator.DEFAULT.eval(writeHead.getExpression(), context);
            result.add(formatCellValue(writeHead, val));
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    private String formatCellValue(ExcelWriteHead writeHead, Object val) {
        if (val == null) {
            return WindConstants.EMPTY;
        }
        Printer formatter = writeHead.getFormatter();
        return formatter == null ? String.valueOf(val) : formatter.print(val, Locale.getDefault());
    }
}
