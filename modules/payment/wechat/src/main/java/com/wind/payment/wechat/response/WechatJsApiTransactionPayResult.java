package com.wind.payment.wechat.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 微信支付 JSAPI 响应
 *
 * @author wuxp
 * @date 2023-10-03 18:28
 **/
@Data
@Accessors(chain = true)
public class WechatJsApiTransactionPayResult {

    /**
     * 应用appid
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 由于package为java保留关键字，因此改为packageValue. 前端使用时记得要更改为package
     */
    private String packageValue;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 签名
     */
    private String paySign;

}
