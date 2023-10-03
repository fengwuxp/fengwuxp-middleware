package com.wind.payment.wechat;

import com.alibaba.fastjson2.JSON;
import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 扫描支付
 * https://pay.weixin.qq.com/docs/merchant/products/native-payment/introduction.html
 *
 * @author wuxp
 * @date 2023-10-03 18:54
 **/
@Slf4j
public class ScanWechatPaymentPlugin extends AbstractWechatPaymentPlugin {

    public ScanWechatPaymentPlugin(String config) {
        super(JSON.parseObject(config, WechatPayPartnerConfig.class));
    }

    public ScanWechatPaymentPlugin(WechatPayPartnerConfig config) {
        super(config);
    }

    @Override
    public PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request) {
        PrePaymentOrderResponse result = new PrePaymentOrderResponse();
        WxPayUnifiedOrderRequest req = new WxPayUnifiedOrderRequest();
        req.setNotifyUrl(request.getNotifyUrl());
        req.setTradeType(WechatPayScene.NATIVE.name());
        req.setBody(normalizationBody(request.getDescription()));
        req.setOutTradeNo(request.getTransactionNo());
        req.setTotalFee(request.getOrderAmount());
        req.setSpbillCreateIp(request.getRemoteIp());
        req.setProductId(request.getTransactionNo());
        req.setTimeExpire(getExpireTimeOrUseDefault(request.getExpireTime()));
        try {
            WxPayNativeOrderResult orderResult = getWxPayService().createOrder(req);
            if (log.isDebugEnabled()) {
                log.debug("微信扫码预下单响应 :{}", orderResult);
            }
            result.setResult(orderResult.getCodeUrl())
                    .setOrderAmount(request.getOrderAmount())
                    .setTransactionNo(request.getTransactionNo())
                    .setUseSandboxEnv(isUseSandboxEnv())
                    .setRawResponse(orderResult);
        } catch (WxPayException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("微信扫码支付交易异常，transactionNo = %s。", request.getTransactionNo()), exception);
        }
        return result;
    }
}
