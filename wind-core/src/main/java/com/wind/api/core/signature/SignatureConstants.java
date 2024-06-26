package com.wind.api.core.signature;

/**
 * 签名相关常量
 *
 * @author wuxp
 * @date 2023-10-18 22:08
 */
final class SignatureConstants {

    private SignatureConstants() {
        throw new AssertionError();
    }

    /**
     * 请求头
     * 32 为随机字符串
     */
    static final String NONCE_HEADER_NAME = "Nonce";

    /**
     * 请求头：时间戳
     * 用于验证签名有效期
     */
    static final String TIMESTAMP_HEADER_NAME = "Timestamp";

    /**
     * 请求头：访问标识
     * 用于交换签名秘钥
     */
    static final String ACCESS_ID_HEADER_NAME = "Access-Id";

    /**
     * 请求头：秘钥版本号
     * 用于标记使用的秘钥对版本
     * 非 AK/SK 访问模式需要
     */
    static final String SECRET_VERSION_HEADER_NAME = "Secret-Version";

    /**
     * 请求头:签名字符串
     * 用于验证请求是否合法
     */
    static final String SIGN_HEADER_NAME = "Sign";

    /**
     * 在签名验证失败时返回服务端的原始签名串
     * 仅在线下联调环境下开启
     */
    static final String DEBUG_SIGN_CONTENT_HEADER_NAME = "Debug-Sign-Content";

    /**
     * 在签名验证失败时返回服务端的查询参数签名串
     * 仅在线下联调环境下开启
     */
    static final String DEBUG_SIGN_QUERY_HEADER_NAME = "Debug-Sign-Query";


}
