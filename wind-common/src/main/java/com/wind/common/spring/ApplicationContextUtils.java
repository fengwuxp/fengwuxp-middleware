package com.wind.common.spring;

import com.wind.common.exception.AssertUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author wuxp
 * @date 2023-09-26 11:32
 **/
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    public static <T> T getBean(Class<T> classType) {
        return getContext().getBean(classType);
    }

    public static String getProperty(String key) {
        return getContext().getEnvironment().getProperty(key, String.class);
    }

    @NonNull
    public static ApplicationContext getContext() {
        AssertUtils.notNull(CONTEXT, "context not init");
        return CONTEXT;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }
}
