package com.wind.security.authentication;

import com.wind.security.AbstractRsaProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * 认证相关配置
 *
 * @author wuxp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WindAuthenticationProperties extends AbstractRsaProperties {

    /**
     * 是否加密手机号码、用户名等登录信息
     */
    private boolean encryptPrincipal = false;

}
