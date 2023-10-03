package com.wind.payment.core;

import com.wind.payment.core.request.PaymentTransactionNoticeRequest;
import com.wind.payment.core.request.PaymentTransactionRefundNoticeRequest;
import com.wind.payment.core.response.QueryTransactionOrderResponse;
import com.wind.payment.core.response.TransactionOrderRefundResponse;

/**
 * 支付交易回调处理
 *
 * @author wuxp
 * @date 2023-10-01 13:40
 **/
public interface PaymentTransactionWebHooker {


    /**
     * 支付通知
     *
     * @param request 支付通知请求参数
     * @return 处理响应
     */
    QueryTransactionOrderResponse paymentNotify(PaymentTransactionNoticeRequest request);

    /**
     * 退款通知
     *
     * @param request 退款通知请求参数
     * @return 处理响应
     */
    TransactionOrderRefundResponse refundNotice(PaymentTransactionRefundNoticeRequest request);

    /**
     * 获取通知处理返回对象
     *
     * @param isSuccess 是否处理成功
     * @return 处理成功响应
     */
    Object getHandleResponse(boolean isSuccess);

}
