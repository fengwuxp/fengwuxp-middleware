package com.wind.common.spring;

import com.wind.common.exception.AssertUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

/**
 * spring 上下文 utils ，非常规用法，为了简化编码
 *
 * @author wuxp
 * @date 2023-09-26 11:32
 **/
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static final AtomicReference<ApplicationContext> APPLICATION_CONTEXT = new AtomicReference<>();

    public static <T> T getBean(Class<T> classType) {
        return getContext().getBean(classType);
    }

    public static String getProperty(String key) {
        return getContext().getEnvironment().getProperty(key, String.class);
    }

    @NonNull
    public static ApplicationContext getContext() {
        AssertUtils.notNull(APPLICATION_CONTEXT.get(), "context not init");
        return APPLICATION_CONTEXT.get();
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.APPLICATION_CONTEXT.set(applicationContext);
    }
}
