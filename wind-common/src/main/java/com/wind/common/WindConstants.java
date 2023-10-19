package com.wind.common;

/**
 * 通用常量类
 *
 * @author wuxp
 */
public final class WindConstants {

    private WindConstants() {
        throw new AssertionError();
    }

    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String DOT = ".";
    public static final String AT = "@";
    public static final String SHARP = "#";
    public static final String DOUBLE_DOT = "..";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    public static final String EMPTY = "";
    public static final String NULL = "null";
    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String CRLF = "\r\n";
    public static final String UNDERLINE = "_";
    public static final String DASHED = "-";
    public static final String COMMA = ",";
    public static final String DELIM_START = "{";
    public static final String DELIM_END = "}";
    public static final String BRACKET_START = "[";
    public static final String BRACKET_END = "]";
    public static final String COLON = ":";
    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_JSON_ARRAY = "[]";


    public static final String DEFAULT_TEXT = "default";


    /**
     * 统一表示成功的 code
     */
    public static final String SUCCESSFUL_CODE = "0";

    /**
     * 未知 text
     */
    public static final String UNKNOWN = "unknown";

    /**
     * 开发环境
     */
    public static final String DEV = "dev";

    /**
     * 集成测试环境
     */
    public static final String SIT = "sit";

    /**
     * 联调（稳定）环境
     */
    public static final String STABLE = "stable";

    /**
     * 用户验收测试环境
     */
    public static final String UAT = "uat";

    /**
     * 预付（灰度）环境
     */
    public static final String PRE = "pre";

    /**
     * 生产环境
     */
    public static final String PROD = "prod";

    /**
     * 应用当前激活的环境
     */
    public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    /**
     * 应用名称
     */
    public static final String SPRING_APPLICATION_NAME = "spring.application.name";

    /**
     * 使用的中间件类型
     */
    public static final String WIND_SERVER_USED_MIDDLEWARE = "wind.server.use.middleware";

    /**
     * 中间在多个应用下共享是，统一配置的配置名称
     */
    public static final String WIND_MIDDLEWARE_SHARE_NAME = "wind.server.middleware.share.name";

    /**
     * Mysql 配置名称
     */
    public static final String WIND_MYSQL_NAME = "wind.mysql.name";

    /**
     * redis 配置名称
     */
    public static final String WIND_REDIS_NAME = "wind.redis.name";

    /**
     * redisson 配置 PropertySource 名称
     */
    public static final String WIND_REDISSON_PROPERTY_SOURCE_NAME = "redissonProperties";

    /**
     * redisson 配置名称
     */
    public static final String SPRING_REDISSON_CONFIG_NAME = "spring.redis.redisson.config";

    /**
     * rocketmq 配置名称
     */
    public static final String WIND_ROCKETMQ_NAME = "wind.rocketmq.name";

    /**
     * oss 配置名称
     */
    public static final String WIND_OSS_NAME = "wind.oss.name";

    /**
     * 配置前缀
     */
    public static final String WIND_SERVER_PROPERTIES_PREFIX = "wind.server";

    public static final String ENABLED_NAME = "enabled";

    public static final String TRUE = "true";

    /**
     * 配置中心支持
     */
    public static final String WIND_SERVER_CONFIG_CENTER_PROPERTIES_PREFIX = "wind.server.config-center";

    /**
     * 统一异常捕获处理 Filter 开启表达式
     */
    public static final String RESTFUL_ERROR_FILTER_EXPRESSION = "wind.server.filter.restful-error-filter";

    /**
     * trace Filter 开启表达式
     */
    public static final String TRACE_FILTER_EXPRESSION = "wind.server.filter.trace-filter";

    /**
     * 控制器日志拦截配置
     */
    public static final String CONTROLLER_ASPECT_LOG_NAME = "wind.server.controller-log-aspect";

    /**
     * 应用配置名称
     */
    public static final String GLOBAL_CONFIG_NAME = "wind-global";

    /**
     * 全局配置分组
     */
    public static final String GLOBAL_CONFIG_GROUP = "GLOBAL";

    /**
     * 应用配置分组
     */
    public static final String APP_CONFIG_GROUP = "APP";

    /**
     * 应用共享配置分组
     */
    public static final String APP_SHARED_CONFIG_GROUP = "APP_SHARED";


    /**
     * traceId
     */
    public static final String TRACE_ID_NAME = "traceId";

    /**
     * http 请求 url 变量
     */
    public static final String HTTP_REQUEST_UR_TRACE_NAME = "requestUrl";

}
