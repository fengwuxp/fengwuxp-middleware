package com.wind.office.excel.export;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.wind.common.WindConstants;
import com.wind.office.excel.ExcelDocumentWriter;
import com.wind.office.excel.metadata.ExcelCellDescriptor;
import com.wind.script.spring.SpringExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

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

    private final List<ExcelCellDescriptor> descriptors;

    private final ExcelWriterSheetBuilder sheetBuilder;

    private DefaultEasyExcelDocumentWriter(List<ExcelCellDescriptor> descriptors, ExcelWriterSheetBuilder sheetBuilder) {
        this.rows = new ArrayList<>(2000);
        this.descriptors = descriptors;
        this.sheetBuilder = sheetBuilder;
    }

    public static DefaultEasyExcelDocumentWriter of(OutputStream output, List<ExcelCellDescriptor> descriptors) {
        List<WriteHandler> handlers = Arrays.asList(
                new CustomHeadColumnWidthStyleStrategy(descriptors),
                new SimpleRowHeightStyleStrategy((short) 25, (short) 25));
        return of(output, descriptors, handlers);
    }

    public static DefaultEasyExcelDocumentWriter of(OutputStream output, List<ExcelCellDescriptor> descriptors, Collection<WriteHandler> handlers) {
        List<String> titles = descriptors.stream().map(ExcelCellDescriptor::getTitle).collect(Collectors.toList());
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        builder.file(output)
                .head(titles.stream().map(Collections::singletonList).collect(Collectors.toList()))
                .needHead(true)
                .charset(StandardCharsets.UTF_8);
        for (WriteHandler handler : handlers) {
            builder.registerWriteHandler(handler);
        }
        return new DefaultEasyExcelDocumentWriter(descriptors, builder.sheet());
    }

    @Override
    public void write(Collection<Object> rows) {
        this.rows.addAll(rows.stream().map(this::format).collect(Collectors.toList()));
    }

    @Override
    public void finish() {
        sheetBuilder.doWrite(rows);
        rows.clear();
    }

    private List<String> format(Object row) {
        List<String> result = new ArrayList<>();
        EvaluationContext context = new StandardEvaluationContext(row);
        for (ExcelCellDescriptor writeHead : descriptors) {
            String expression = writeHead.getExpression();
            Object val = StringUtils.hasText(expression) ? SpringExpressionEvaluator.DEFAULT.eval(expression, context) : row;
            result.add(formatCellValue(writeHead, val));
        }
        return result;
    }

    private String formatCellValue(ExcelCellDescriptor descriptor, Object val) {
        if (val == null) {
            return WindConstants.EMPTY;
        }
        return descriptor.getPrinter().print(val, Locale.getDefault());
    }
}
