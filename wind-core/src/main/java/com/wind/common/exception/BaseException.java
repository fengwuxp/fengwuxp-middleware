package com.wind.common.exception;


import com.wind.common.message.MessageFormatter;
import com.wind.common.message.MessagePlaceholder;
import lombok.Getter;

/**
 * 通用基础业务异常，不同的业务场景可以继承该类做扩展
 *
 * @author wuxp
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 319556802147084526L;

    private static final MessageFormatter MESSAGE_FORMATTER = MessageFormatter.java();

    private final ExceptionCode code;

    private final MessagePlaceholder messagePlaceholder;

    public BaseException(String message) {
        this(DefaultExceptionCode.COMMON_ERROR, message);
    }

    public BaseException(ExceptionCode code, String message) {
        this(code, message, null);
    }

    public BaseException(ExceptionCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.messagePlaceholder = null;
    }

    public BaseException(MessagePlaceholder placeholder) {
        this(DefaultExceptionCode.COMMON_ERROR, placeholder);
    }

    public BaseException(ExceptionCode code, MessagePlaceholder placeholder) {
        this(code, placeholder, null);
    }

    public BaseException(ExceptionCode code, MessagePlaceholder placeholder, Throwable cause) {
        super(MESSAGE_FORMATTER.format(placeholder.getPattern(), placeholder.getArgs()), cause);
        this.code = code;
        this.messagePlaceholder = placeholder;
    }

    public String getTextCode() {
        return code.getCode();
    }

    public static BaseException badRequest(String message) {
        return new BaseException(DefaultExceptionCode.BAD_REQUEST, message);
    }

    public static BaseException unAuthorized(String message) {
        return new BaseException(DefaultExceptionCode.UNAUTHORIZED, message);
    }

    public static BaseException forbidden(String message) {
        return new BaseException(DefaultExceptionCode.FORBIDDEN, message);
    }

    public static BaseException notFound(String message) {
        return new BaseException(DefaultExceptionCode.NOT_FOUND, message);
    }

    public static BaseException common(String message) {
        return new BaseException(message);
    }

    public static BaseException badRequest(MessagePlaceholder message) {
        return new BaseException(DefaultExceptionCode.BAD_REQUEST, message);
    }

    public static BaseException unAuthorized(MessagePlaceholder message) {
        return new BaseException(DefaultExceptionCode.UNAUTHORIZED, message);
    }

    public static BaseException forbidden(MessagePlaceholder message) {
        return new BaseException(DefaultExceptionCode.FORBIDDEN, message);
    }

    public static BaseException notFound(MessagePlaceholder message) {
        return new BaseException(DefaultExceptionCode.NOT_FOUND, message);
    }

    public static BaseException common(MessagePlaceholder message) {
        return new BaseException(message);
    }

}
