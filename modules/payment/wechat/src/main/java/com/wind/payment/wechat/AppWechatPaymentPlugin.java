package com.wind.payment.wechat;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import com.wind.payment.wechat.response.WechatAppTransactionPayResult;
import lombok.extern.slf4j.Slf4j;

/**
 * APP 支付
 * 参见：https://pay.weixin.qq.com/docs/merchant/products/in-app-payment/introduction.html
 *
 * @author wuxp
 * @date 2023-10-03 17:56
 **/
@Slf4j
public class AppWechatPaymentPlugin extends AbstractWechatPaymentPlugin {

    public AppWechatPaymentPlugin(String config) {
        super(JSON.parseObject(config, WechatPayPartnerConfig.class));
    }

    public AppWechatPaymentPlugin(WechatPayPartnerConfig config) {
        super(config);
    }

    @Override
    public PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request) {
        PrePaymentOrderResponse result = new PrePaymentOrderResponse();
        WxPayUnifiedOrderRequest req = new WxPayUnifiedOrderRequest();
        req.setNotifyUrl(request.getNotifyUrl());
        req.setTradeType(WechatPayScene.APP.name());
        req.setBody(normalizationBody(request.getDescription()));
        req.setOutTradeNo(request.getTransactionNo());
        req.setTotalFee(request.getOrderAmount());
        req.setSpbillCreateIp(request.getRemoteIp());
        req.setProductId(request.getTransactionNo());
        req.setTimeExpire(getExpireTimeOrUseDefault(request.getExpireTime()));
        try {
            WxPayAppOrderResult orderResult = getWxPayService().createOrder(req);
            if (log.isDebugEnabled()) {
                log.debug("微信 APP 预下单响应 :{}", orderResult);
            }
            WechatAppTransactionPayResult response = new WechatAppTransactionPayResult();
            response.setAppId(orderResult.getAppId())
                    .setTimeStamp(orderResult.getTimeStamp())
                    .setNonceStr(orderResult.getNonceStr())
                    .setPackageValue(orderResult.getPackageValue())
                    .setPartnerId(orderResult.getPartnerId())
                    .setPrepayId(orderResult.getPrepayId())
                    .setSign(orderResult.getSign());
            result.setResult(response)
                    .setOrderAmount(request.getOrderAmount())
                    .setTransactionNo(request.getTransactionNo())
                    .setUseSandboxEnv(isUseSandboxEnv())
                    .setRawResponse(response);
        } catch (WxPayException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("微信 App 支付交易异常，transactionNo = %s。", request.getTransactionNo()), exception);
        }
        return result;
    }
}
