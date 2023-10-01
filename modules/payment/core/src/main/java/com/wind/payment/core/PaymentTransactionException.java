package com.wind.payment.core;

import com.wind.common.exception.BaseException;
import com.wind.common.exception.ExceptionCode;

/**
 * 交易相关异常
 *
 * @author wuxp
 * @date 2023-09-30 20:05
 **/
public class PaymentTransactionException extends BaseException {

    private static final long serialVersionUID = 3450136622980599458L;

    public PaymentTransactionException(String message) {
        super(message);
    }

    public PaymentTransactionException(ExceptionCode code, String message) {
        super(code, message);
    }

    public PaymentTransactionException(ExceptionCode code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
