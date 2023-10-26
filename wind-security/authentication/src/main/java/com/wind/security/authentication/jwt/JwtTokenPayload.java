package com.wind.security.authentication.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * jwt token 解析结果
 * @author wuxp
 * @date 2023-09-26 09:35
 **/
@AllArgsConstructor
@Getter
public final class JwtTokenPayload implements Serializable {

    private static final long serialVersionUID = 1801730423764136024L;

    /**
     * jwt subject，一般是用户 ID
     */
    private String subject;

    /**
     * 用户信息，如果是 refresh token，该字段为空
     */
    @Nullable
    private final JwtUser user;

    /**
     * token 过期时间戳
     */
    private final Long expireTime;


    public Long getUserId() {
        return Long.parseLong(subject);
    }

}
