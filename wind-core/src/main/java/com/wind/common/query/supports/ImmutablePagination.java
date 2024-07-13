package com.wind.common.query.supports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * 不可以变的分页对象
 *
 * @author wuxp
 * @date 2024-05-06 18:38
 **/
@Getter
@AllArgsConstructor
public class ImmutablePagination<T> implements Pagination<T> {

    private static final long serialVersionUID = -4678352910174889294L;

    private final long total;

    private final List<T> records;

    private final int queryPage;

    private final int querySize;

    private final QueryType queryType;

    /**
     * 为了给序列化框架使用，提供一个空构造
     */
    ImmutablePagination() {
        this(0, Collections.emptyList(), 0, 0, QueryType.QUERY_BOTH);
    }
}