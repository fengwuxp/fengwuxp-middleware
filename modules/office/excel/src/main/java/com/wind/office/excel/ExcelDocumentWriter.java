package com.wind.office.excel;

import java.util.Collection;

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
    void write(Object row);

    default void write(Collection<Object> rows) {
        rows.forEach(this::write);
    }

    /**
     * 写入完成
     */
    void finish();
}
