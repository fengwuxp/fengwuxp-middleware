package com.wind.security.mfa;

import org.springframework.security.core.AuthenticationException;

/**
 * @author wuxp
 * @date 2024-03-06 11:06
 **/
public class MfaUnAuthenticationException extends AuthenticationException {

    public MfaUnAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MfaUnAuthenticationException(String msg) {
        super(msg);
    }
}
