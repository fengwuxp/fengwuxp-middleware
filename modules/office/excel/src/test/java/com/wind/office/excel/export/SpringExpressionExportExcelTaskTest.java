package com.wind.office.excel.export;

import com.google.common.collect.ImmutableMap;
import com.wind.office.core.OfficeTaskState;
import com.wind.office.excel.ExcelDocumentWriter;
import com.wind.office.excel.ExportExcelDataFetcher;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class SpringExpressionExportExcelTaskTest {


    private SpringExpressionExportExcelTask task;


    @BeforeEach
    void setup() throws Exception {
        List<ExportExcelTaskInfo.ExcelHead> heads = Arrays.asList(
                mockExcelHead("name"),
                mockExcelHead("age"),
                mockExcelHead("sex")
        );
        Path filepath = Paths.get(Objects.requireNonNull(SpringExpressionExportExcelTaskTest.class.getResource("/")).getPath(), "test.xlsx");
        Files.deleteIfExists(filepath);
        ExcelDocumentWriter writer = new DefaultEasyExcelDocumentWriter(Files.newOutputStream(filepath),
                heads.stream().map(ExportExcelTaskInfo.ExcelHead::getTitle).collect(Collectors.toList()));
        task = new SpringExpressionExportExcelTask(ExportExcelTaskInfo.of("test", heads, writer), mockExcelDataFetcher());
    }

    @Test
    void testTask() {
        task.run();
        Assertions.assertEquals(OfficeTaskState.COMPLETED, task.getState());
    }

    private ExportExcelDataFetcher<Object> mockExcelDataFetcher() {
        return (page, size) -> {
            if (page >= 2) {
                return Collections.emptyList();
            }
            List<Object> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(mockRowData());
            }
            return result;
        };
    }

    private static ImmutableMap<String, String> mockRowData() {
        return ImmutableMap.of("name", RandomStringUtils.randomAlphanumeric(12),
                "age", RandomStringUtils.randomNumeric(2));
    }

    private ExportExcelTaskInfo.ExcelHead mockExcelHead(String name) {
        return new ExportExcelTaskInfo.ExcelHead(name, String.format("['%s']", name), null);
    }

}