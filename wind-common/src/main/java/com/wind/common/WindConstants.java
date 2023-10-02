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
     * 配置前缀
     */
    public static final String WIND_SERVER_PROPERTIES_PREFIX = "wind.server";


    /**
     * 控制器日志拦截开启表达式
     */
    public static final String CONTROLLER_ASPECT_LOG_EXPRESSION = "wind.server.controller-aspect-log.expression";


}
