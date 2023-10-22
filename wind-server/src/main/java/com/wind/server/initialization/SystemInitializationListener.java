package com.wind.server.initialization;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StopWatch;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 系统初始化器执行入口
 *
 * @author wuxp
 * @date 2023-10-22 07:49
 **/
@Slf4j
@AllArgsConstructor
public class SystemInitializationListener implements ApplicationListener<ApplicationStartedEvent> {

    private final Collection<SystemInitializer> initializers;

    private final AtomicBoolean flag = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(@Nonnull ApplicationStartedEvent event) {
        if (flag.get()) {
            return;
        }
        log.info("begin execute SystemInitializer");
        StopWatch watch = new StopWatch();
        watch.start("system-initialization-task");
        try {
            initializers.stream()
                    .filter(SystemInitializer::requiredInitialize)
                    .forEach(SystemInitializer::initialize);
        } catch (Exception exception) {
            log.error("execute SystemInitializer error", exception);
        }
        watch.stop();
        log.info("SystemInitializer execute end, use times = {} seconds", watch.getTotalTimeSeconds());
    }
}
