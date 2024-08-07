package com.wind.logging.logback.mask;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.wind.mask.ObjectDataMasker;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * 日志脱敏
 *
 * @author wuxp
 * @date 2024-08-07 15:47
 **/
public class MaskingMessageConverter extends ClassicConverter {

    public final static ObjectDataMasker LOG_MASKER = new ObjectDataMasker();

    @Override
    public String convert(ILoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (ObjectUtils.isEmpty(argumentArray)) {
            // TODO 字符串处理
            return event.getFormattedMessage();
        }
        try {
            Object[] args = Arrays.stream(argumentArray)
                    .map(LOG_MASKER::maskWithDeepCopy)
                    .toArray(Object[]::new);
            return MessageFormatter.arrayFormat(event.getMessage(), args).getMessage();
        } catch (Throwable throwable) {
            // TODO
            return event.getFormattedMessage();
        }
    }

}