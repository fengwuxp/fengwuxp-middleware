package com.wind.payment.alipay;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import com.wind.payment.core.util.PaymentTransactionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * APP 支付
 * 参见：https://opendocs.alipay.com/open/204/105051?pathHash=b91b9616
 *
 * @author wuxp
 * @date 2023-10-01 09:50
 **/
@Slf4j
public class AppAlipayPaymentPlugin extends AbstractAlipayPaymentPlugin {

    /**
     * 产品代码
     */
    private static final String ALI_APP_PAY_PRODUCT_CODE = "QUICK_MSECURITY_PAY";

    public AppAlipayPaymentPlugin(String config) {
        this(JSON.parseObject(config, AliPayPartnerConfig.class));
    }

    public AppAlipayPaymentPlugin(AliPayPartnerConfig config) {
        super(config);
    }

    @Override
    public PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request) {
        AlipayTradeAppPayRequest req = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setProductCode(ALI_APP_PAY_PRODUCT_CODE);
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
            // 这里和普通的接口调用不同，使用的是 sdkExecute
            AlipayTradeAppPayResponse response = getAlipayClient().sdkExecute(req);
            if (log.isDebugEnabled()) {
                log.debug("支付响应 :{}", response);
            }
            if (response.isSuccess()) {
                result.setTransactionNo(request.getTransactionNo())
                        .setOutTransactionNo(response.getTradeNo())
                        .setUseSandboxEnv(this.isUseSandboxEnv())
                        .setOrderAmount(request.getOrderAmount())
                        .setResult(response.getBody())
                        .setRawResponse(response);
            } else {
                throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝 App 支付交易失败，transactionNo = %s。" +
                        ERROR_PATTERN, request.getTransactionNo(), response.getCode(), response.getMsg()));
            }

        } catch (AlipayApiException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝 App 支付交易异常，transactionNo = %s。", request.getTransactionNo()), exception);
        }

        return result;
    }
}
