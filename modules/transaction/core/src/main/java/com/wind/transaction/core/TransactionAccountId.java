package com.wind.transaction.core;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author wuxp
 * @date 2024-05-14 16:26
 **/
@EqualsAndHashCode
@ToString
public class TransactionAccountId {

    @Getter
    private final String id;

    private final Object userId;

    @Getter
    private final String type;

    private TransactionAccountId(String id, Object userId, String type) {
        AssertUtils.hasText(id, "argument id must not empty");
        AssertUtils.notNull(userId, "argument userId must not null");
        AssertUtils.hasText(type, "argument type must not empty");
        this.id = id;
        this.userId = userId;
        this.type = type;
    }

    public static TransactionAccountId of(String id, Object userId) {
        return new TransactionAccountId(id, userId, WindConstants.DEFAULT_TEXT);
    }

    public static TransactionAccountId of(String id, Object userId, String type) {
        return new TransactionAccountId(id, userId, type);
    }

    @SuppressWarnings("unchecked")
    public <T> T getUserId() {
        return (T) userId;
    }

}
