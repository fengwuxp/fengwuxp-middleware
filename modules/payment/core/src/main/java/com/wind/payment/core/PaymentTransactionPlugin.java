package com.wind.payment.core;

import com.wind.payment.core.request.PrePaymentOrderRequest;
import com.wind.payment.core.request.QueryTransactionOrderRefundRequest;
import com.wind.payment.core.request.QueryTransactionOrderRequest;
import com.wind.payment.core.request.TransactionOrderRefundRequest;
import com.wind.payment.core.response.PrePaymentOrderResponse;
import com.wind.payment.core.response.QueryTransactionOrderResponse;
import com.wind.payment.core.response.TransactionOrderRefundResponse;

/**
 * 支付交易插件、根据不同的平台提供支付相关能力
 *
 * @author wuxp
 * @date 2023-09-30 19:06
 **/
public interface PaymentTransactionPlugin extends PaymentTransactionWebHooker {

    /**
     * 预下(支付)单
     * 预下单失败则抛出异常 {@link PaymentTransactionException}
     *
     * @param request 预下单请求
     * @return 预下单结果
     */
    PrePaymentOrderResponse preOrder(PrePaymentOrderRequest request);

    /**
     * 查询交易订单
     * 查询失败则抛出异常 {@link PaymentTransactionException}
     *
     * @param request 查询订单请求
     * @return 查询响应
     */
    QueryTransactionOrderResponse queryTransactionOrder(QueryTransactionOrderRequest request);

    /**
     * 交易单退款
     * 退款失败则抛出异常 {@link PaymentTransactionException}
     *
     * @param request 退款请求
     * @return 退款响应
     */
    TransactionOrderRefundResponse transactionOrderRefund(TransactionOrderRefundRequest request);

    /**
     * 查询交易退款
     * 查询退款失败则抛出异常 {@link PaymentTransactionException}
     *
     * @param request 退款请求
     * @return 退款响应
     */
    TransactionOrderRefundResponse queryTransactionOrderRefund(QueryTransactionOrderRefundRequest request);
}
