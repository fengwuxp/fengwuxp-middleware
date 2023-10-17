package com.wind.server.web.exception;


import com.wind.common.exception.BaseException;
import com.wind.server.web.restful.RestfulApiRespFactory;
import com.wind.server.web.supports.ApiResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wind.common.WindConstants.WIND_SERVER_PROPERTIES_PREFIX;


/**
 * 默认全局异常处理
 *
 * @author wxup
 */
@Slf4j
@ConditionalOnProperty(prefix = WIND_SERVER_PROPERTIES_PREFIX, name = "enable-global-exception", havingValue = "true", matchIfMissing = true)
@RestControllerAdvice()
public class DefaultGlobalExceptionHandler {

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ApiResp<Void> handleValidationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        return RestfulApiRespFactory.badRequest(constraintViolations.stream().map(constraintViolation -> {
            String message = constraintViolation.getMessage();
            String propertyPath = constraintViolation.getPropertyPath().toString();
            return MessageFormat.format("{0}：{1}", propertyPath, message);
        }).collect(Collectors.joining("、")));

    }

    /**
     * {@link org.springframework.web.bind.annotation.RequestParam}
     * {@link org.springframework.web.bind.annotation.RequestHeader}
     * 等参数绑定失败异常
     */
    @ExceptionHandler(value = ServletRequestBindingException.class)
    @ResponseBody
    public ApiResp<Void> handleServletRequestBindingException(ServletRequestBindingException exception) {
        return RestfulApiRespFactory.badRequest(exception.getMessage());
    }

    /**
     * controller bind data 的异常
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ApiResp<Void> handleMethodBindException(BindException exception) {
        FieldError fieldError = exception.getFieldError();
        if (fieldError == null) {
            return RestfulApiRespFactory.badRequest(exception.getMessage());
        }
        String message = fieldError.getDefaultMessage();
        return RestfulApiRespFactory.badRequest(message);
    }


    /**
     * 404 的异常就会被这个方法捕获
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResp<Void> handle404Error(Exception exception) {
        return RestfulApiRespFactory.notFound(exception.getMessage());
    }


    /**
     * 处理业务异常
     */
    @ExceptionHandler({BaseException.class})
    @ResponseBody
    public ApiResp<Integer> handleBusinessServiceException(BaseException exception) {
        log.error("业务异常，code：{}，errorMessage：{}", exception.getTextCode(), exception.getMessage(), exception);
        return RestfulApiRespFactory.withThrowable(exception);
    }


    /**
     * 统一兜底异常处理，如果前面都没有捕获到，将进入该方法
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ApiResp<Void> handleException(Exception exception) {
        Throwable throwable = exception;
        log.error("捕获到异常：{}，errorMessage：{}", exception.getClass().getName(), exception.getMessage(), exception);
        if (throwable instanceof UndeclaredThrowableException) {
            // 获取真正的异常
            InvocationTargetException invocationTargetException = (InvocationTargetException) ((UndeclaredThrowableException) throwable).getUndeclaredThrowable();
            throwable = invocationTargetException.getTargetException();
        }
        return RestfulApiRespFactory.error(throwable.getMessage());
    }
}
