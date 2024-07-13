package com.wind.office.excel.export;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.style.column.AbstractHeadColumnWidthStyleStrategy;
import com.wind.office.excel.metadata.ExcelCellDescriptor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 自定义表头宽度
 *
 * @author wuxp
 * @date 2024-04-27 14:14
 **/
@AllArgsConstructor
public class CustomHeadColumnWidthStyleStrategy extends AbstractHeadColumnWidthStyleStrategy {

    private final List<ExcelCellDescriptor> descriptors;

    private final int defaultWidth;

    public CustomHeadColumnWidthStyleStrategy(List<ExcelCellDescriptor> descriptors) {
        this(descriptors, 20);
    }

    @Override
    protected Integer columnWidth(Head head, Integer columnIndex) {
        return descriptors.get(columnIndex).getWidth(defaultWidth);
    }
}
