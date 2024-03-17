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

class SpringExpressionExportExcelTaskTest {

    private SpringExpressionExportExcelTask task;

    @BeforeEach
    void setup() throws Exception {
        List<ExcelWriteHead> heads = Arrays.asList(
                mockExcelHead("name"),
                mockExcelHead("age"),
                mockExcelHead("sex")
        );
        Path filepath = Paths.get(Objects.requireNonNull(SpringExpressionExportExcelTaskTest.class.getResource("/")).getPath(), "test.xlsx");
        Files.deleteIfExists(filepath);
        ExcelDocumentWriter writer = DefaultEasyExcelDocumentWriter.simple(Files.newOutputStream(filepath), heads);
        task = new SpringExpressionExportExcelTask(ExportExcelTaskInfo.of("test", writer), mockExcelDataFetcher());
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

    private ExcelWriteHead mockExcelHead(String name) {
        return new ExcelWriteHead(name, String.format("['%s']", name), null);
    }

}