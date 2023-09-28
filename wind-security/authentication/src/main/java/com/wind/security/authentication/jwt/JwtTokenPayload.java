package com.wind.security.authentication.jwt;

import lombok.Data;
import lombok.Getter;

/**
 * @author wuxp
 * @date 2023-09-26 09:35
 **/
@Data
@Getter
public final class JwtTokenPayload<T> {

    private final String userId;

    private final T user;

    /**
     * token 过期时间戳
     */
    private final Long expireTime;
}
