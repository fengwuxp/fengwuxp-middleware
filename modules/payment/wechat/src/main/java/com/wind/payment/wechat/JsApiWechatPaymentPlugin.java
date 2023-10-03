package com.wind.payment.wechat;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import com.wind.payment.wechat.response.WechatJsApiTransactionPayResult;
import lombok.extern.slf4j.Slf4j;

/**
 * JsApi 支付
 * 参见：https://pay.weixin.qq.com/docs/merchant/products/jsapi-payment/introduction.html
 *
 * @author wuxp
 * @date 2023-10-03 17:56
 **/
@Slf4j
public class JsApiWechatPaymentPlugin extends AbstractWechatPaymentPlugin {

    public JsApiWechatPaymentPlugin(String config) {
        super(JSON.parseObject(config, WechatPayPartnerConfig.class));
    }

    public JsApiWechatPaymentPlugin(WechatPayPartnerConfig config) {
        super(config);
    }

    @Override
    public PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request) {
        PrePaymentOrderResponse result = new PrePaymentOrderResponse();
        WxPayUnifiedOrderRequest req = new WxPayUnifiedOrderRequest();
        req.setNotifyUrl(request.getNotifyUrl());
        req.setTradeType(WechatPayScene.JSAPI.name());
        req.setBody(normalizationBody(request.getDescription()));
        req.setOutTradeNo(request.getTransactionNo());
        req.setTotalFee(request.getOrderAmount());
        req.setSpbillCreateIp(request.getRemoteIp());
        req.setProductId(request.getTransactionNo());
        req.setTimeExpire(getExpireTimeOrUseDefault(request.getExpireTime()));
        req.setOpenid(request.getUserId());
        try {
            WxPayMpOrderResult orderResult = getWxPayService().createOrder(req);
            if (log.isDebugEnabled()) {
                log.debug("微信 JsApi 预下单响应 :{}", orderResult);
            }
            WechatJsApiTransactionPayResult response = new WechatJsApiTransactionPayResult();
            response.setAppId(orderResult.getAppId())
                    .setTimeStamp(orderResult.getTimeStamp())
                    .setNonceStr(orderResult.getNonceStr())
                    .setPackageValue(orderResult.getPackageValue())
                    .setPaySign(orderResult.getPaySign())
                    .setSignType(orderResult.getSignType());
            result.setResult(response)
                    .setRawResponse(response)
                    .setOrderAmount(request.getOrderAmount())
                    .setTransactionNo(request.getTransactionNo())
                    .setUseSandboxEnv(isUseSandboxEnv());
        } catch (WxPayException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("微信 JsApi 支付交易异常，transactionNo = %s。", request.getTransactionNo()), exception);
        }
        return result;
    }
}
