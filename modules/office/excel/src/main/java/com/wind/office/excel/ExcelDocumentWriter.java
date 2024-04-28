package com.wind.office.excel;

import java.util.Collection;
import java.util.Collections;

/**
 * excel writer
 *
 * @author wuxp
 * @date 2024-01-02 20:15
 **/
public interface ExcelDocumentWriter {

    /**
     * 按照行写入数据
     *
     * @param row 行数据
     */
    default void write(Object row) {
        write(Collections.singletonList(row));
    }

    /**
     * 批量写入数据
     *
     * @param rows 行数据列表
     */
    void write(Collection<Object> rows);

    /**
     * 写入完成
     */
    void finish();
}
