package com.wind.common;

/**
 * @author wuxp
 * @date 2023-10-18 22:08
 **/
public final class WindHttpConstants {

    private WindHttpConstants() {
        throw new AssertionError();
    }

    /**
     * http request 来源 ip
     */
    public static final String HTTP_REQUEST_IP_ATTRIBUTE_NAME = "requestSourceIp";

    /**
     * api 请求账号
     */
    public static final String API_SECRET_ACCOUNT_ATTRIBUTE_NAME = "Wind-Attribute-Api-Secret-Account";

    /**
     * http request User-Agent header name
     */
    public static final String HTTP_USER_AGENT_HEADER_NAME = "User-Agent";

}
