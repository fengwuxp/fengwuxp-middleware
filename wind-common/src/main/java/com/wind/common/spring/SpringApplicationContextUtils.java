package com.wind.common.spring;

import com.wind.common.exception.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * spring 上下文 utils ，非常规用法，为了简化编码
 * 请谨慎使用
 *
 * @author wuxp
 * @date 2023-12-21 18:19
 **/
@Slf4j
public class SpringApplicationContextUtils implements ApplicationContextAware {

    private static final AtomicReference<ApplicationContext> CONTEXT_HOLDER = new AtomicReference<>();

    public static String getProperty(String key) {
        return requireApplicationContext().getEnvironment().getProperty(key, String.class);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        AssertUtils.notNull(applicationContext, "argument applicationContext must not null");
        CONTEXT_HOLDER.set(applicationContext);
    }

    public static void publishEvent(ApplicationEvent event) {
        requireApplicationContext().publishEvent(event);
    }

    static ApplicationContext requireApplicationContext() {
        ApplicationContext result = CONTEXT_HOLDER.get();
        AssertUtils.notNull(result, "spring application context not init");
        return result;
    }
}
