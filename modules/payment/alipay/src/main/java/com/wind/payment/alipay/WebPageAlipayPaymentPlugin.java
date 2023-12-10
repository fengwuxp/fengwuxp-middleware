package com.wind.payment.alipay;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.alipay.response.AliPayPageTransactionPayResult;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import com.wind.payment.core.util.PaymentTransactionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 电脑网站支付
 * 参见：https://opendocs.alipay.com/open/270/105898
 *
 * @author wuxp
 * @date 2023-10-01 17:20
 **/
@Slf4j
public class WebPageAlipayPaymentPlugin extends AbstractAlipayPaymentPlugin {

    /**
     * 产品代码
     */
    private static final String ALI_WEB_PAGE_PAY_PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";

    public WebPageAlipayPaymentPlugin(String config) {
        this(JSON.parseObject(config, AliPayPartnerConfig.class));
    }

    public WebPageAlipayPaymentPlugin(AliPayPartnerConfig config) {
        super(config);
    }

    @Override
    public PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request) {
        AlipayTradePagePayRequest req = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setProductCode(ALI_WEB_PAGE_PAY_PRODUCT_CODE);
        model.setOutTradeNo(request.getTransactionNo());
        model.setBody(normalizationBody(request.getDescription()));
        model.setTimeoutExpress(getExpireTimeOrUseDefault(request.getExpireTime()));
        model.setSubject(request.getSubject());
        model.setTotalAmount(PaymentTransactionUtils.feeToYun(request.getOrderAmount()).toString());
        req.setBizModel(model);
        req.setNotifyUrl(request.getNotifyUrl());
        req.setReturnUrl(request.getReturnUrl());
        if (log.isDebugEnabled()) {
            log.debug("支付请求参数：{}", req);
        }
        PrePaymentOrderResponse result = new PrePaymentOrderResponse();
        try {
            AlipayTradePagePayResponse response = getAlipayClient().pageExecute(req);
            if (log.isDebugEnabled()) {
                log.debug("支付响应 :{}", response);
            }
            if (response.isSuccess()) {
                AliPayPageTransactionPayResult tradePayResult = new AliPayPageTransactionPayResult();
                tradePayResult.setOrderInfo(response.getBody())
                        .setTransactionNo(response.getOutTradeNo())
                        .setOutTransactionNo(response.getTradeNo())
                        .setSellerId(response.getSellerId())
                        .setTotalAmount(response.getTotalAmount())
                        .setTradeNo(response.getTradeNo());
                result.setTransactionNo(response.getOutTradeNo())
                        .setOutTransactionNo(response.getTradeNo())
                        .setUseSandboxEnv(this.isUseSandboxEnv())
                        .setOrderAmount(PaymentTransactionUtils.yuanToFee(response.getTotalAmount()))
                        .setResult(tradePayResult)
                        .setRawResponse(response);
            } else {
                throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝电脑网站支付交易失败，transactionNo = %s。" +
                        ERROR_PATTERN, request.getTransactionNo(), response.getCode(), response.getMsg()));
            }
        } catch (AlipayApiException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝电脑网站支付交易异常，transactionNo = %s。", request.getTransactionNo()), exception);
        }

        return result;
    }
}
