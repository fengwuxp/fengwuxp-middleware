package com.wind.common.exception;


/**
 * 通用基础业务异常
 *
 * @author wuxp
 */
public class BaseException extends RuntimeException {

    private final ExceptionCode code;

    public BaseException(String message) {
        this(ExceptionCode.DEFAULT_ERROR, message);
    }

    public BaseException(ExceptionCode code, String message) {
        this(code, message, null);
    }

    public BaseException(ExceptionCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ExceptionCode getCode() {
        return code;
    }

    public String getTextCode() {
        return code.getCode();
    }
}
