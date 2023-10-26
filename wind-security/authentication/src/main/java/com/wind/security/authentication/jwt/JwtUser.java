package com.wind.security.authentication.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * jwt token User
 *
 * @author wuxp
 * @date 2023-10-26 12:49
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtUser implements Serializable {

    private static final long serialVersionUID = -7270843983936135796L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户属性
     */
    private Map<String, Object> attributes = Collections.emptyMap();

    public JwtUser(Long id, String userName) {
        this(id, userName, Collections.emptyMap());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
