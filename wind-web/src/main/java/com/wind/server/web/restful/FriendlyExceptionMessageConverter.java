package com.wind.server.web.restful;

import com.wind.common.exception.BaseException;
import com.wind.common.i18n.SpringI18nMessageUtils;
import com.wind.common.message.MessagePlaceholder;

/**
 * 友好的异常消息转换器
 *
 * @author wuxp
 * @date 2024-09-19 16:24
 **/
public interface FriendlyExceptionMessageConverter {

    /**
     * 转换异常消息
     *
     * @param throwable 异常信息
     * @return 转换后便于用户理解的消息
     */
    String convert(Throwable throwable);

    default String convert(Throwable throwable, String defaultMessage) {
        String result = convert(throwable);
        return result == null ? defaultMessage : result;
    }

    static FriendlyExceptionMessageConverter none(){
        return Throwable::getMessage;
    }

    /**
     * 异常消息国际化
     *
     * @dosc https://www.yuque.com/suiyuerufeng-akjad/wind/vzoygfi6ehphzhvh
     */
    static FriendlyExceptionMessageConverter i18n() {
        return throwable -> {
            if (throwable instanceof BaseException) {
                MessagePlaceholder placeholder = ((BaseException) throwable).getMessagePlaceholder();
                if (placeholder != null) {
                    return SpringI18nMessageUtils.getMessage(placeholder.getPattern(), placeholder.getArgs());
                }
            }
            return SpringI18nMessageUtils.getMessage(throwable.getMessage());
        };
    }
}
