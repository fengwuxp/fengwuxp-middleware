package com.wind.payment.wechat;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import lombok.Data;

/**
 * 微信支付配置
 *
 * @author wuxp
 * @date 2023-10-03 09:34
 **/
@Data
public class WechatPayPartnerConfig {

    /**
     * 设置微信公众号或者小程序等的appid
     */
    private String appId;

    /**
     * AppSecret
     */
    protected String appSecret;

    /**
     * 合作者(商户号)
     */
    protected String partner;

    /**
     * 商户加密key，需要在微信商户平台进行设置
     */
    protected String partnerSecret;

    /**
     * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除
     */
    private String subAppId;

    /**
     * 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除
     */
    private String subMchId;

    /**
     * apiclient_cert.p12 文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    private String keyPath;

    /**
     * 是否使用沙箱环境
     */
    private boolean useSandboxEnv = false;

    /**
     * 签名类型
     */
    private String signType = WxPayConstants.SignType.MD5;
}
