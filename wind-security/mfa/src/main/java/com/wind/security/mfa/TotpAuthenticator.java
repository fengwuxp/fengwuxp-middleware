package com.wind.security.mfa;

/**
 * Time-based One-time Password(基于时间的一次性密码) 认证服务
 *
 * @author wuxp
 * @date 2024-03-05 15:16
 **/
public interface TotpAuthenticator {

    /**
     * 动态验证码验证
     *
     * @param userId 用户唯一标识
     * @param code   2fa 验证码
     * @return 验证结果
     */
    boolean verify(String userId, String code);

    /**
     * 生成 2fa 绑定二维码
     *
     * @param userId   用户唯一标识
     * @param showName 用户展示名称
     * @param issuer   2fa 机构地址
     * @return 二维码 base64 字符串
     */
    String generateBindingQrCode(String userId, String showName, String issuer);
}
