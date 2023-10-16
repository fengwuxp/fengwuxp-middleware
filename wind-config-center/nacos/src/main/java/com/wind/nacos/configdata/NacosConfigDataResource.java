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

package com.wind.nacos.configdata;

import com.wind.nacos.NacosConfigProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.logging.Log;
import org.springframework.boot.context.config.ConfigDataResource;
import org.springframework.boot.context.config.Profiles;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author freeman
 * @since 2021.0.1.0
 */
public class NacosConfigDataResource extends ConfigDataResource {

    private final NacosConfigProperties properties;

    private final boolean optional;

    private final Profiles profiles;

    private final Log log;

    private final NacosItemConfig config;

    public NacosConfigDataResource(NacosConfigProperties properties, boolean optional,
                                   Profiles profiles, Log log, NacosItemConfig config) {
        this.properties = properties;
        this.optional = optional;
        this.profiles = profiles;
        this.log = log;
        this.config = config;
    }

    public NacosConfigProperties getProperties() {
        return this.properties;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public String getProfiles() {
        return StringUtils.collectionToCommaDelimitedString(getAcceptedProfiles());
    }

    List<String> getAcceptedProfiles() {
        return this.profiles.getAccepted();
    }

    public Log getLog() {
        return this.log;
    }

    public NacosItemConfig getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NacosConfigDataResource that = (NacosConfigDataResource) o;
        return optional == that.optional && Objects.equals(properties, that.properties)
                && Objects.equals(profiles, that.profiles)
                && Objects.equals(log, that.log) && Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, optional, profiles, log, config);
    }

    @Override
    public String toString() {
        return "NacosConfigDataResource{" + "properties=" + properties + ", optional="
                + optional + ", profiles=" + profiles + ", config=" + config + '}';
    }

    @Data
    @Accessors(chain = true)
    public static class NacosItemConfig {

        private String group;

        private String dataId;

        private String suffix;

        private boolean refreshEnabled;

        private String preference;

        public NacosItemConfig() {
        }

        public NacosItemConfig(String group, String dataId, String suffix,
                               boolean refreshEnabled, String preference) {
            this.group = group;
            this.dataId = dataId;
            this.suffix = suffix;
            this.refreshEnabled = refreshEnabled;
            this.preference = preference;
        }
    }

}
