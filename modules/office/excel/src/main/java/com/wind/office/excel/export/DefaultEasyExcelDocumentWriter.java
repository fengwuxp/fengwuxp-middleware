package com.wind.office.excel.export;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.wind.office.excel.ExcelDocumentWriter;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    private final ExcelWriterSheetBuilder sheetBuilder;

    public DefaultEasyExcelDocumentWriter(OutputStream output, List<String> titles) {
        this(output, titles, Arrays.asList(
                new SimpleColumnWidthStyleStrategy(25),
                new SimpleRowHeightStyleStrategy((short) 25, (short) 25)), 1000);
    }

    public DefaultEasyExcelDocumentWriter(OutputStream output, List<String> titles, Collection<WriteHandler> handlers) {
        this(output, titles, handlers, 1000);
    }

    public DefaultEasyExcelDocumentWriter(OutputStream output, List<String> titles, Collection<WriteHandler> handlers, int size) {
        ExcelWriterBuilder builder = new ExcelWriterBuilder();
        this.sheetBuilder = builder.file(output)
                .head(titles.stream().map(Collections::singletonList).collect(Collectors.toList()))
                .needHead(true)
                .charset(StandardCharsets.UTF_8)
                .sheet();
        for (WriteHandler handler : handlers) {
            sheetBuilder.registerWriteHandler(handler);
        }
        this.rows = new ArrayList<>(size);
    }


    @Override
    public void write(Object row) {
        rows.add(row);
    }

    @Override
    public void write(Collection<Object> rows) {
        this.rows.addAll(rows);
    }

    @Override
    public void finish() {
        sheetBuilder.doWrite(rows);
        rows.clear();
    }

}
