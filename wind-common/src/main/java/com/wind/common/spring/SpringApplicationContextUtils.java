package com.wind.common.spring;

import com.wind.common.exception.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * spring 上下文 utils ，非常规用法，为了简化编码
 * 请谨慎使用
 *
 * @author wuxp
 * @date 2023-12-21 18:19
 **/
@Slf4j
public class SpringApplicationContextUtils {

    private final ApplicationContext context;

    private static final AtomicReference<SpringApplicationContextUtils> HOLDER = new AtomicReference<>();

    private SpringApplicationContextUtils(ApplicationContext context) {
        this.context = context;
    }

    public static String getProperty(String key) {
        return getContext().getEnvironment().getProperty(key, String.class);
    }

    public static void publishEvent(ApplicationEvent event) {
        getContext().publishEvent(event);
    }

    public static void refreshContext(ApplicationContext context) {
        AssertUtils.notNull(context, "argument context must not null");
        HOLDER.set(new SpringApplicationContextUtils(context));
    }

    static ApplicationContext getContext() {
        SpringApplicationContextUtils utils = HOLDER.get();
        AssertUtils.notNull(utils, "context utils not init, please call use #refreshContext");
        AssertUtils.notNull(utils.context, "spring application context not init");
        return utils.context;
    }
}
