package com.wind.office.excel.export;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.wind.office.core.AbstractOfficeHandleTask;
import com.wind.office.core.OfficeTaskState;
import com.wind.office.core.formatter.MapFormatter;
import com.wind.office.excel.ExcelDocumentDescriptor;
import com.wind.office.excel.ExportExcelDataFetcher;
import com.wind.script.spring.SpringExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.format.Formatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * excel 导出任务
 *
 * @author wuxp
 * @date 2023-10-27 18:28
 **/
public class ExportExcelHandleTask extends AbstractOfficeHandleTask {

    private final ExportExcelDataFetcher<?> fetcher;

    private final ExcelDocumentDescriptor descriptor;

    /**
     * 缓存处理结果
     */
    private final List<List<List<String>>> cacheResult = new ArrayList<>();

    /**
     * 缓存 sheets
     */
    private final List<WriteSheet> sheets = new ArrayList<>();

    public ExportExcelHandleTask(String name, ExcelDocumentDescriptor descriptor, ExportExcelDataFetcher<?> fetcher) {
        super(name);
        this.descriptor = descriptor;
        this.fetcher = fetcher;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public OfficeTaskState getState() {
        return null;
    }

    @Override
    public LocalDateTime getBeginTime() {
        return null;
    }

    @Nullable
    @Override
    public LocalDateTime getEndTime() {
        return null;
    }

    @Override
    public void handle() {
        int queryPage = 1;
        while (true) {
            List<?> result = fetcher.fetch(queryPage, descriptor.getFetchSize());
            if (result.size() < descriptor.getFetchSize()) {
                break;
            }
            for (Object o : result) {


            }
        }
    }

    private List<String> convert(Object o) {
        List<String> result = new ArrayList<>();
        EvaluationContext context = new StandardEvaluationContext(o);
        descriptor.getHeaders().forEach(cell -> {
            Object val = SpringExpressionEvaluator.DEFAULT.eval(cell.getExpression(), context);
            if (val != null) {

            }
        });
        return result;
    }

    private Formatter<?> getFormatter(ExcelDocumentDescriptor.Cell cell) {
        if (cell.getMapFormatterSource() != null) {
            return new MapFormatter(cell.getMapFormatterSource());
        }
        if (StringUtils.hasText(cell.getNumStylePattern())) {
            return new NumberStyleFormatter(cell.getNumStylePattern());
        }
        return cell.getFormatter();
    }

    private WriteSheet createSheet(int index) {
        WriteSheet sheet = new WriteSheet();
        sheet.setSheetNo(index);
        List<List<String>> titles = descriptor.getHeaders().stream()
                .map(cell -> Collections.singletonList(cell.getTitle()))
                .collect(Collectors.toList());
        sheet.setHead(titles);
        return sheet;
    }
}
