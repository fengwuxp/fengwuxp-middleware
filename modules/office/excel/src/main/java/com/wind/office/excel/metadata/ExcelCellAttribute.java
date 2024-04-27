package com.wind.office.excel.metadata;

import org.springframework.lang.Nullable;

/**
 * excel单元格属性
 *
 * @author wuxp
 * @date 2024-04-27 14:21
 **/
public interface ExcelCellAttribute<T> {

    /**
     * @return 属性值
     */
    @Nullable
    T getValue();
}
