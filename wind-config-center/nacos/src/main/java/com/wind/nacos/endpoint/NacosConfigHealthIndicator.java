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

package com.wind.nacos.endpoint;

import com.alibaba.nacos.api.config.ConfigService;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * The {@link HealthIndicator} for Nacos Config.
 *
 * @author xiaojing
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
@AllArgsConstructor
public class NacosConfigHealthIndicator extends AbstractHealthIndicator {

    private final ConfigService configService;

    /**
     * status up .
     */
    private static final String STATUS_UP = "UP";

    /**
     * status down .
     */
    private static final String STATUS_DOWN = "DOWN";

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // Just return "UP" or "DOWN"
        String status = configService.getServerStatus();
        // Set the status to Builder
        builder.status(status);
        switch (status) {
            case STATUS_UP:
                builder.up();
                break;
            case STATUS_DOWN:
                builder.down();
                break;
            default:
                builder.unknown();
                break;
        }
    }

}
