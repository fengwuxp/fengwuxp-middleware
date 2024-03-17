package com.wind.office.excel;

import java.util.List;

/**
 * excel 导出数据 fetcher
 *
 * @author wuxp
 * @date 2023-10-27 18:29
 **/
public interface ExportExcelDataFetcher<T> {

    int DEFAULT_TOTAL = -1;

    /**
     * 统计总数
     *
     * @return -1 表示不关心总数，查询到没有为止
     */
    default int count() {
        return DEFAULT_TOTAL;
    }

    /**
     * 抓取数据
     *
     * @param page 开始抓取数据的页码 从 1 开始
     * @param size 抓取大小
     * @return 数据集合，查询到结果集小于 size 为止
     */
    List<T> fetch(int page, int size);
}
