package com.wind.server.web;

/**
 * web 相关常量
 *
 * @author wuxp
 * @date 2023-09-23 07:10
 **/
public final class WindWebConstants {

    private WindWebConstants() {
        throw new AssertionError();
    }

    /**
     * http request 来源 ip
     */
    public static final String HTTP_REQUEST_IP_ATTR_NAME = "requestSourceIp";

}
