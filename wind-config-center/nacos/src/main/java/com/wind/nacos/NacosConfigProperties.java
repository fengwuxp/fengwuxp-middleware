package com.wind.nacos;

import com.alibaba.spring.util.PropertySourcesUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.nacos.api.PropertyKeyConst.ACCESS_KEY;
import static com.alibaba.nacos.api.PropertyKeyConst.CLUSTER_NAME;
import static com.alibaba.nacos.api.PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT;
import static com.alibaba.nacos.api.PropertyKeyConst.CONFIG_RETRY_TIME;
import static com.alibaba.nacos.api.PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG;
import static com.alibaba.nacos.api.PropertyKeyConst.ENCODE;
import static com.alibaba.nacos.api.PropertyKeyConst.ENDPOINT;
import static com.alibaba.nacos.api.PropertyKeyConst.ENDPOINT_PORT;
import static com.alibaba.nacos.api.PropertyKeyConst.MAX_RETRY;
import static com.alibaba.nacos.api.PropertyKeyConst.NAMESPACE;
import static com.alibaba.nacos.api.PropertyKeyConst.PASSWORD;
import static com.alibaba.nacos.api.PropertyKeyConst.RAM_ROLE_NAME;
import static com.alibaba.nacos.api.PropertyKeyConst.SECRET_KEY;
import static com.alibaba.nacos.api.PropertyKeyConst.SERVER_ADDR;
import static com.alibaba.nacos.api.PropertyKeyConst.USERNAME;

/**
 * @author wuxp
 * @date 2023-10-15 14:08
 **/
@Data
@ConfigurationProperties(NacosConfigProperties.PREFIX)
public class NacosConfigProperties implements EnvironmentAware {

    /**
     * Prefix of {@link NacosConfigProperties}.
     */
    public static final String PREFIX = "spring.cloud.nacos.config";

    /**
     * COMMAS , .
     */
    public static final String COMMAS = ",";

    /**
     * SEPARATOR , .
     */
    public static final String SEPARATOR = "[,]";

    /**
     * Nacos default namespace .
     */
    public static final String DEFAULT_NAMESPACE = "public";

    private static final Pattern PATTERN = Pattern.compile("-(\\w)");

    private static final Logger log = LoggerFactory.getLogger(NacosConfigProperties.class);

    @JsonIgnore
    private Environment environment;

    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    private void overrideFromEnv() {
        if (environment == null) {
            return;
        }
        if (ObjectUtils.isEmpty(this.getServerAddr())) {
            String serverAddr = environment.resolvePlaceholders("${spring.cloud.nacos.config.server-addr:}");
            if (ObjectUtils.isEmpty(serverAddr)) {
                serverAddr = environment.resolvePlaceholders("${spring.cloud.nacos.server-addr:127.0.0.1:8848}");
            }
            this.setServerAddr(serverAddr);
        }
        if (ObjectUtils.isEmpty(this.getUsername())) {
            this.setUsername(environment.resolvePlaceholders("${spring.cloud.nacos.username:}"));
        }
        if (ObjectUtils.isEmpty(this.getPassword())) {
            this.setPassword(environment.resolvePlaceholders("${spring.cloud.nacos.password:}"));
        }
    }

    /**
     * nacos config server address.
     */
    private String serverAddr;

    /**
     * the nacos authentication username.
     */
    private String username;

    /**
     * the nacos authentication password.
     */
    private String password;

    /**
     * encode for nacos config content.
     */
    private String encode;

    /**
     * nacos config group, group is config data meta info.
     */
    private String group = "DEFAULT_GROUP";

    /**
     * the suffix of nacos config dataId, also the file extension of config content.
     */
    private String fileExtension = "properties";

    /**
     * timeout for get config from nacos.
     */
    private int timeout = 3000;

    /**
     * nacos maximum number of tolerable server reconnection errors.
     */
    private String maxRetry;

    /**
     * nacos get config long poll timeout.
     */
    private String configLongPollTimeout;

    /**
     * nacos get config failure retry time.
     */
    private String configRetryTime;

    /**
     * If you want to pull it yourself when the program starts to get the configuration
     * for the first time, and the registered Listener is used for future configuration
     * updates, you can keep the original code unchanged, just add the system parameter:
     * enableRemoteSyncConfig = "true" ( But there is network overhead); therefore we
     * recommend that you use {@link com.alibaba.nacos.api.config.ConfigService#getConfigAndSignListener} directly.
     */
    private boolean enableRemoteSyncConfig = false;

    /**
     * endpoint for Nacos, the domain name of a service, through which the server address
     * can be dynamically obtained.
     */
    private String endpoint;

    /**
     * namespace, separation configuration of different environments.
     */
    private String namespace;

    /**
     * access key for namespace.
     */
    private String accessKey;

    /**
     * secret key for namespace.
     */
    private String secretKey;

    /**
     * role name for aliyun ram.
     */
    private String ramRoleName;

    /**
     * context path for nacos config server.
     */
    private String contextPath;

    /**
     * nacos config cluster name.
     */
    private String clusterName;

    /**
     * nacos config dataId name.
     */
    private String name;

    /**
     * the master switch for refresh configuration, it default opened(true).
     */
    private boolean refreshEnabled = true;


    /**
     * assemble properties for configService. (cause by rename : Remove the interference
     * of auto prompts when writing,because autocue is based on get method.
     *
     * @return properties
     */
    public Properties assembleConfigServiceProperties() {
        Properties properties = new Properties();
        properties.put(SERVER_ADDR, Objects.toString(this.serverAddr, ""));
        properties.put(USERNAME, Objects.toString(this.username, ""));
        properties.put(PASSWORD, Objects.toString(this.password, ""));
        properties.put(ENCODE, Objects.toString(this.encode, ""));
        properties.put(NAMESPACE, this.resolveNamespace());
        properties.put(ACCESS_KEY, Objects.toString(this.accessKey, ""));
        properties.put(SECRET_KEY, Objects.toString(this.secretKey, ""));
        properties.put(RAM_ROLE_NAME, Objects.toString(this.ramRoleName, ""));
        properties.put(CLUSTER_NAME, Objects.toString(this.clusterName, ""));
        properties.put(MAX_RETRY, Objects.toString(this.maxRetry, ""));
        properties.put(CONFIG_LONG_POLL_TIMEOUT, Objects.toString(this.configLongPollTimeout, ""));
        properties.put(CONFIG_RETRY_TIME, Objects.toString(this.configRetryTime, ""));
        properties.put(ENABLE_REMOTE_SYNC_CONFIG, Objects.toString(this.enableRemoteSyncConfig, ""));
        String endpoint = Objects.toString(this.endpoint, "");
        if (endpoint.contains(":")) {
            int index = endpoint.indexOf(":");
            properties.put(ENDPOINT, endpoint.substring(0, index));
            properties.put(ENDPOINT_PORT, endpoint.substring(index + 1));
        } else {
            properties.put(ENDPOINT, endpoint);
        }

        enrichNacosConfigProperties(properties);
        return properties;
    }

    /**
     * refer
     * https://github.com/alibaba/spring-cloud-alibaba/issues/2872
     * https://github.com/alibaba/spring-cloud-alibaba/issues/2869 .
     */
    private String resolveNamespace() {
        if (DEFAULT_NAMESPACE.equals(this.namespace)) {
            log.info("set nacos config namespace 'public' to ''");
            return "";
        } else {
            return Objects.toString(this.namespace, "");
        }
    }

    private void enrichNacosConfigProperties(Properties nacosConfigProperties) {
        if (environment == null) {
            return;
        }
        Map<String, Object> properties = PropertySourcesUtils.getSubProperties((ConfigurableEnvironment) environment, PREFIX);
        properties.forEach((k, v) -> nacosConfigProperties.putIfAbsent(resolveKey(k), String.valueOf(v)));
    }

    private String resolveKey(String key) {
        Matcher matcher = PATTERN.matcher(key);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
