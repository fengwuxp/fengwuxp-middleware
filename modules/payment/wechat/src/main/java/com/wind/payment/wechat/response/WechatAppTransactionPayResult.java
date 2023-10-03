package com.wind.payment.wechat.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信支付 APP 响应
 *
 * @author wuxp
 * @date 2023-10-03 18:01
 **/
@Data
@Accessors(chain = true)
public class WechatAppTransactionPayResult {

    /**
     * 应用APPID
     */
    private String appId;

    /**
     * 签名
     */
    private String sign;

    /**
     * 预支付交易会话标识
     */
    private String prepayId;

    /**
     * 商户号
     */
    private String partnerId;

    /**
     * 由于package为java保留关键字，因此改为packageValue. 前端使用时记得要更改为package
     */
    private String packageValue;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

}
