package com.wind.security.authentication.jwt;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author wuxp
 * @date 2023-09-26 09:35
 **/
@Data
@Getter
public final class JwtTokenPayload implements Serializable {

    private static final long serialVersionUID = 1801730423764136024L;

    /**
     * 用户 id
     */
    private final String userId;

    /**
     * 用户信息
     */
    private final Object user;

    /**
     * token 过期时间戳
     */
    private final Long expireTime;

    @SuppressWarnings("unchecked")
    public <T> T getUser() {
        return (T) user;
    }
}
