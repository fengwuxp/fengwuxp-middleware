package com.wind.payment.alipay;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.payment.alipay.notice.AlipayAsyncNoticeRequest;
import com.wind.payment.core.PaymentTransactionException;
import com.wind.payment.core.PaymentTransactionPlugin;
import com.wind.payment.core.enums.ExpireTimeType;
import com.wind.payment.core.enums.PaymentTransactionState;
import com.wind.payment.core.request.PaymentTransactionNoticeRequest;
import com.wind.payment.core.request.PaymentTransactionRefundNoticeRequest;
import com.wind.payment.core.request.QueryTransactionOrderRefundRequest;
import com.wind.payment.core.request.QueryTransactionOrderRequest;
import com.wind.payment.core.request.TransactionOrderRefundRequest;
import com.wind.payment.core.response.QueryTransactionOrderResponse;
import com.wind.payment.core.response.TransactionOrderRefundResponse;
import com.wind.payment.core.util.PaymentTransactionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 支付宝支付插件抽象类
 *
 * @author wuxp
 * @date 2023-10-01 08:48
 **/
@Slf4j
public abstract class AbstractAlipayPaymentPlugin implements PaymentTransactionPlugin {

    /**
     * 支付结果处理成功返回码
     */
    private static final String PAYMENT_RESULT_HANDLE_SUCCESS_RETURN_CODE = "success";

    /**
     * 支付结果处理失败返回码
     */
    private static final String PAYMENT_RESULT_HANDLE_FAILURE_RETURN_CODE = "failure";

    static final String ERROR_PATTERN = "errorCode：%s，errorMessage：%s";

    private static final String ALI_PAY_DEV = "alipaydev";

    private final AliPayPartnerConfig config;

    @Getter
    private final AlipayClient alipayClient;

    protected AbstractAlipayPaymentPlugin(AliPayPartnerConfig config) {
        AssertUtils.hasLength(config.getAppId(), "alipay AppId must not empty");
        AssertUtils.hasLength(config.getPartner(), "alipay Partner must not empty");
        AssertUtils.hasLength(config.getServiceUrl(), "alipay ServiceUrl must not empty");
        AssertUtils.hasLength(config.getRsaPrivateKey(), "alipay RsaPrivateKey must not empty");
        AssertUtils.hasLength(config.getRsaPublicKey(), "alipay RsaPublicKey must not empty");
        this.config = config;
        this.alipayClient = new DefaultAlipayClient(
                config.getServiceUrl(),
                config.getAppId(),
                config.getRsaPrivateKey(),
                "json",
                config.getCharset(),
                config.getRsaPublicKey(),
                config.getEncryptType().name());
    }

    @Override
    public QueryTransactionOrderResponse queryTransactionOrder(QueryTransactionOrderRequest request) {
        AlipayTradeQueryRequest req = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setTradeNo(request.getOutTransactionNo());
        model.setOutTradeNo(request.getTransactionNo());
        req.setBizModel(model);
        QueryTransactionOrderResponse result = new QueryTransactionOrderResponse();
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(req);
            if (log.isDebugEnabled()) {
                log.debug("查询支付宝支付结果 :{}", response.getBody());
            }
            if (response.isSuccess()) {
                int buyerPayAmount = PaymentTransactionUtils.yuanToFee(response.getBuyerPayAmount());
                result.setOutTransactionNo(response.getTradeNo())
                        .setOutTransactionNo(response.getOutTradeNo())
                        .setOrderAmount(PaymentTransactionUtils.yuanToFee(response.getTotalAmount()))
                        .setBuyerPayAmount(buyerPayAmount)
                        .setReceiptAmount(PaymentTransactionUtils.yuanToFee(response.getReceiptAmount()))
                        .setUseSandboxEnv(this.isUseSandboxEnv())
                        .setTransactionState(this.transformTradeState(response.getTradeStatus(), buyerPayAmount))
                        .setRawResponse(response);
            } else {
                throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("查询支付宝交易单失败，transactionNo = %s。" +
                        ERROR_PATTERN, request.getTransactionNo(), response.getCode(), response.getMsg()));
            }
        } catch (AlipayApiException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("查询支付宝交易单异常，transactionNo = %s", request.getTransactionNo()), exception);
        }
        return result;
    }

    @Override
    public TransactionOrderRefundResponse transactionOrderRefund(TransactionOrderRefundRequest request) {
        AlipayTradeRefundRequest req = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(request.getOutTransactionNo());
        model.setTradeNo(request.getTransactionNo());
        model.setOutRequestNo(request.getTransactionRefundNo());
        model.setRefundAmount(PaymentTransactionUtils.feeToYun(request.getRefundAmount()).toString());
        model.setRefundReason(request.getRefundReason());
        if (log.isDebugEnabled()) {
            log.debug("支付宝退款请求参数 {}", model);
        }
        req.setBizModel(model);
        AssertUtils.hasLength(request.getRefundNotifyUrl(), "refund notify url mist not empty");
        req.setNotifyUrl(request.getRefundNotifyUrl());
        if (log.isDebugEnabled()) {
            log.debug("支付宝退款回调 URL ->[{}]", request.getRefundNotifyUrl());
        }

        TransactionOrderRefundResponse result = new TransactionOrderRefundResponse();
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(req);
            if (log.isDebugEnabled()) {
                log.debug("支付宝退款响应, {}", response);
            }
            if (response.isSuccess()) {
                result.setTransactionNo(request.getTransactionRefundNo())
                        .setTransactionRefundNo(response.getOutTradeNo())
                        .setOutTransactionRefundNo(response.getTradeNo())
                        .setOrderAmount(PaymentTransactionUtils.yuanToFee(response.getRefundFee()))
                        .setOrderAmount(request.getOrderAmount())
                        .setTransactionState(PaymentTransactionState.WAIT_REFUND)
                        .setRawResponse(response);
            } else {
                throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝交易退款失败，transactionNo = %s。" +
                        ERROR_PATTERN, request.getTransactionNo(), response.getCode(), response.getMsg()));
            }
            result.setRawResponse(response);
        } catch (AlipayApiException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("支付宝交易退款异常，transactionNo = %s", request.getTransactionNo()), exception);
        }

        return result;
    }

    @Override
    public TransactionOrderRefundResponse queryTransactionOrderRefund(QueryTransactionOrderRefundRequest request) {
        TransactionOrderRefundResponse result = new TransactionOrderRefundResponse();
        AlipayTradeFastpayRefundQueryRequest req = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setTradeNo(request.getTransactionNo());
        model.setOutTradeNo(request.getOutTransactionNo());
        model.setOutRequestNo(request.getRequestRefundNo());
        req.setBizModel(model);
        try {
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(req);
            if (log.isDebugEnabled()) {
                log.debug("查询支付宝退款响应, {}", response);
            }
            if (response.isSuccess()) {
                int refundAmount = PaymentTransactionUtils.yuanToFee(response.getRefundAmount());
                int orderAmount = PaymentTransactionUtils.yuanToFee(response.getTotalAmount());
                result.setTransactionNo(request.getTransactionNo())
                        .setTransactionRefundNo(response.getOutTradeNo())
                        .setOutTransactionRefundNo(response.getOutRequestNo())
                        .setRefundAmount(refundAmount)
                        .setOrderAmount(orderAmount)
                        .setTransactionState(Objects.equals(refundAmount, orderAmount) ? PaymentTransactionState.REFUNDED : PaymentTransactionState.PARTIAL_REFUND)
                        .setRawResponse(response);
            } else {
                throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR,
                        String.format("查询支付宝交易退款失败，transactionNo = %s。" + ERROR_PATTERN, request.getTransactionNo(), response.getCode(), response.getMsg()));
            }
        } catch (AlipayApiException exception) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, String.format("查询支付宝交易退款异常，transactionNo = %s", request.getTransactionNo()), exception);
        }
        return result;
    }

    @Override
    public QueryTransactionOrderResponse paymentNotify(PaymentTransactionNoticeRequest request) {
        verifyPaymentNotifyRequest(request);
        QueryTransactionOrderResponse result = new QueryTransactionOrderResponse();
        AlipayAsyncNoticeRequest noticeRequest = request.getRawRequest();
        result.setOutTransactionNo(noticeRequest.getTrade_no())
                .setTransactionNo(noticeRequest.getOut_trade_no())
                .setOrderAmount(PaymentTransactionUtils.yuanToFee(noticeRequest.getTotal_amount()));
        AliPayTransactionState tradeState = noticeRequest.getTrade_status();
        Integer buyerPayAmount;
        BigDecimal payAmount = noticeRequest.getBuyer_pay_amount();
        if (payAmount == null) {
            buyerPayAmount = request.getOrderAmount();
        } else {
            buyerPayAmount = payAmount.intValue();
        }
        result.setTransactionState(this.transformTradeState(tradeState.name(), buyerPayAmount))
                .setBuyerPayAmount(buyerPayAmount)
                .setUseSandboxEnv(this.isUseSandboxEnv())
                .setPayerAccount(noticeRequest.getBuyer_logon_id())
                .setRawResponse(noticeRequest);
        BigDecimal receiptAmount = noticeRequest.getReceipt_amount();
        if (receiptAmount != null) {
            // TODO 实收金额验证
            result.setReceiptAmount(receiptAmount.intValue());
        }
        return result;
    }

    @Override
    public TransactionOrderRefundResponse refundNotice(PaymentTransactionRefundNoticeRequest request) {
        verifyRefundNotifyRequest(request);
        // 退款处理订单通知
        TransactionOrderRefundResponse result = new TransactionOrderRefundResponse();
        AlipayAsyncNoticeRequest noticeRequest = request.getRawRequest();
        result.setTransactionRefundNo(request.getTransactionRefundNo());
        result.setOutTransactionRefundNo(noticeRequest.getOut_biz_no());
        result.setOrderAmount(PaymentTransactionUtils.yuanToFee(noticeRequest.getTotal_amount()));
        result.setRefundAmount(PaymentTransactionUtils.yuanToFee(noticeRequest.getRefund_fee()));
        return result;
    }

    @Override
    public Object getHandleResponse(boolean isSuccess) {
        return isSuccess ? PAYMENT_RESULT_HANDLE_SUCCESS_RETURN_CODE : PAYMENT_RESULT_HANDLE_FAILURE_RETURN_CODE;
    }

    /**
     * @return 是否使用沙箱环境
     */
    protected boolean isUseSandboxEnv() {
        return this.config.getServiceUrl().contains(ALI_PAY_DEV);
    }

    /**
     * @param state           支付宝交易状态
     * @param buyerPaidAmount 买家实付金额
     * @return 支付交易状态
     */
    private PaymentTransactionState transformTradeState(String state, int buyerPaidAmount) {
        AliPayTransactionState aliPayTradeState = Enum.valueOf(AliPayTransactionState.class, state);
        if (AliPayTransactionState.WAIT_BUYER_PAY.equals(aliPayTradeState)) {
            return PaymentTransactionState.WAIT_PAY;
        }

        if (AliPayTransactionState.TRADE_SUCCESS.equals(aliPayTradeState)) {
            return PaymentTransactionState.SUCCESS;
        }

        if (AliPayTransactionState.TRADE_FINISHED.equals(aliPayTradeState)) {
            return PaymentTransactionState.SUCCESS;
        }

        if (AliPayTransactionState.TRADE_CLOSED.equals(aliPayTradeState)) {
            if (buyerPaidAmount == 0) {
                // 买家实付金额为0 说明未支付
                return PaymentTransactionState.CLOSED;
            }
            return PaymentTransactionState.UNKNOWN;
        }

        return PaymentTransactionState.UNKNOWN;
    }

    /**
     * 验证支付宝支付通知请求
     */
    private void verifyPaymentNotifyRequest(PaymentTransactionNoticeRequest request) {
        // 参数验证
        String tradeNo = request.getTransactionNo();
        AlipayAsyncNoticeRequest rawRequest = request.getRawRequest();
        BigDecimal orderAmount = PaymentTransactionUtils.feeToYun(request.getOrderAmount());
        boolean paramVerify = Objects.equals(tradeNo, rawRequest.getOut_trade_no())
                && Objects.equals(orderAmount, rawRequest.getTotal_amount());
        AssertUtils.isTrue(paramVerify, () -> String.format("支付宝支付通知，【%s】参数验证失:%s", tradeNo, rawRequest));
        verifySign(rawRequest);
    }


    /**
     * 验证支付宝退款通知请求
     */
    private void verifyRefundNotifyRequest(PaymentTransactionRefundNoticeRequest request) {
        // 参数验证
        Map<String, String> params = request.getRawRequest();
        String transactionRefundNo = request.getTransactionRefundNo();
        BigDecimal refundAmount = PaymentTransactionUtils.feeToYun(request.getRefundAmount());

        boolean paramVerify = Objects.equals(transactionRefundNo, params.get("out_trade_no"))
                && Objects.equals(refundAmount.toString(), params.get("refund_fee"));
        AssertUtils.isTrue(paramVerify, String.format("支付宝退款通知，【%s】参数验证失败，%s", transactionRefundNo, params));
        verifySign(request.getRawRequest());
    }


    /**
     * 验证签名
     *
     * @param request 回调参数
     */
    private void verifySign(AlipayAsyncNoticeRequest request) {
        // 签名验证
        Map<String, String> signParams = new HashMap<>();
        // 深 Copy
        Map<String, String> params = JSON.parseObject(JSON.toJSONString(request), new TypeReference<Map<String, String>>() {
        });
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() != null) {
                signParams.put(key, entry.getValue());
            }
        }
        AliPayPartnerConfig.EncryptType signType = AliPayPartnerConfig.EncryptType.valueOf(params.get("sign_type"));
        try {
            // 切记 rsaPublicKey 是支付宝的公钥，请去 open.alipay.com 对应应用下查看。
            boolean result = AlipaySignature.rsaCheckV1(signParams, config.getRsaPublicKey(), config.getCharset(), signType.name());
            AssertUtils.isTrue(result, "支付宝通知签名验证失败");
        } catch (AlipayApiException e) {
            throw new PaymentTransactionException(DefaultExceptionCode.COMMON_ERROR, "支付宝支付通知签名验证异常", e);
        }
    }

    static String normalizationBody(String description) {
        return StringUtils.abbreviate(description, 128);
    }

    static String getExpireTimeOrUseDefault(String expireTime) {
        // 默认 30 分钟过期
        return StringUtils.isNotEmpty(expireTime) ? expireTime : ExpireTimeType.MINUTE.getAliRuleDesc(30);
    }
}
