/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wind.nacos.refresh;


import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.wind.nacos.NacosConfigProperties;
import com.wind.nacos.NacosPropertySourceRepository;
import com.wind.nacos.client.NacosPropertySource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * On application start up, NacosContextRefresher add nacos listeners to all application
 * level dataIds, when there is a change in the data, listeners will refresh
 * configurations.
 *
 * @author juven.xuxb
 * @author pbting
 * @author freeman
 */
@Slf4j
public class NacosContextRefresher
        implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private static final AtomicLong REFRESH_COUNT = new AtomicLong(0);

    private final NacosConfigProperties nacosConfigProperties;

    private final boolean isRefreshEnabled;

    private final NacosRefreshHistory nacosRefreshHistory;

    private final ConfigService configService;

    private ApplicationContext applicationContext;

    private final AtomicBoolean ready = new AtomicBoolean(false);

    private final Map<String, Listener> listenerMap = new ConcurrentHashMap<>(16);

    public NacosContextRefresher(NacosConfigProperties properties, ConfigService configService, NacosRefreshHistory refreshHistory) {
        this.nacosConfigProperties = properties;
        this.configService = configService;
        this.nacosRefreshHistory = refreshHistory;
        this.isRefreshEnabled = this.nacosConfigProperties.isRefreshEnabled();
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        // many Spring context
        if (this.ready.compareAndSet(false, true)) {
            this.registerNacosListenersForApplications();
        }
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * register Nacos Listeners.
     */
    private void registerNacosListenersForApplications() {
        if (isRefreshEnabled()) {
            for (NacosPropertySource propertySource : NacosPropertySourceRepository
                    .getAll()) {
                if (!propertySource.isRefreshable()) {
                    continue;
                }
                String dataId = propertySource.getDataId();
                registerNacosListener(propertySource.getGroup(), dataId);
            }
        }
    }

    private void registerNacosListener(final String groupKey, final String dataKey) {
        String key = NacosPropertySourceRepository.getMapKey(dataKey, groupKey);
        Listener listener = listenerMap.computeIfAbsent(key,
                lst -> new AbstractSharedListener() {
                    @Override
                    public void innerReceive(String dataId, String group,
                                             String configInfo) {
                        refreshCountIncrement();
                        nacosRefreshHistory.addRefreshRecord(dataId, group, configInfo);
                        applicationContext.publishEvent(
                                new RefreshEvent(this, null, "Refresh Nacos config"));
                        if (log.isDebugEnabled()) {
                            log.debug(String.format(
                                    "Refresh Nacos config group=%s,dataId=%s,configInfo=%s",
                                    group, dataId, configInfo));
                        }
                    }
                });
        try {
            configService.addListener(dataKey, groupKey, listener);
            log.info("[Nacos Config] Listening config: dataId={}, group={}", dataKey,
                    groupKey);
        } catch (NacosException e) {
            log.warn(String.format(
                    "register fail for nacos listener ,dataId=[%s],group=[%s]", dataKey,
                    groupKey), e);
        }
    }

    public boolean isRefreshEnabled() {
        if (null == nacosConfigProperties) {
            return isRefreshEnabled;
        }
        // Compatible with older configurations
        if (nacosConfigProperties.isRefreshEnabled() && !isRefreshEnabled) {
            return false;
        }
        return isRefreshEnabled;
    }

    public static long getRefreshCount() {
        return REFRESH_COUNT.get();
    }

    public static void refreshCountIncrement() {
        REFRESH_COUNT.incrementAndGet();
    }

}
