package com.wind.payment.alipay.notice;

import com.wind.payment.alipay.AliPayPartnerConfig;
import com.wind.payment.alipay.AliPayTransactionState;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付宝异步回调通知请求参数
 * https://opendocs.alipay.com/open/204/105301?pathHash=fef00e6d
 *
 * @author wuxp
 * @date 2023-10-01 14:49
 **/
@Data
public class AlipayAsyncNoticeRequest {

    /**
     * 通知时间。通知的发送时间。格式为 yyyy-MM-dd HH:mm:ss。
     * 例如：2020-12-27 06:20:30
     */
    private Date notify_time;

    /**
     * 通知类型。枚举值：trade_status_sync。
     */
    private String notify_type;

    /**
     * 通知校验 ID
     */
    private String notify_id;

    /**
     * 签名类型。商家生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐使用 RSA2（如果开发者手动验签，不使用 SDK 验签，可以不传此参数）。
     */
    private AliPayPartnerConfig.EncryptType sign_type;

    /**
     * 签名。可查看异步返回结果的验签（如果开发者手动验签，不使用 SDK 验签，可以不传此参数）
     */
    private String sign;

    /**
     * 支付宝交易号。支付宝交易凭证号。
     */
    private String trade_no;

    /**
     * 开发者的 app_id。支付宝分配给开发者的应用 APPID。
     */
    private String app_id;

    /**
     * 开发者的 app_id，在服务商调用的场景下为授权方的 app_id。
     */
    private String auth_app_id;

    /**
     * 商户订单号。
     */
    private String out_trade_no;

    /**
     * 商家业务号。商家业务 ID，主要是退款通知中返回退款申请的流水号。
     */
    private String out_biz_no;

    /**
     * 买家支付宝用户号。买家支付宝账号对应的支付宝唯一用户号。
     */
    private String buyer_id;

    /**
     * 买家支付宝账号。
     */
    private String buyer_logon_id;

    /**
     * 卖家支付宝用户号。
     */
    private String seller_id;

    /**
     * 卖家支付宝账号。
     */
    private String seller_email;

    /**
     * 交易状态。咨询目前所处的状态
     */
    private AliPayTransactionState trade_status;

    /**
     * 订单金额。本次交易支付的订单金额，单位为人民币（元）。支持小数点后两位。。
     */
    private BigDecimal total_amount;

    /**
     * 实收金额。商家在交易中实际收到的款项，单位为人民币（元）。支持小数点后两位。
     */
    private BigDecimal receipt_amount;

    /**
     * 开票金额。用户在交易中支付的可开发票的金额。支持小数点后两位
     */
    private BigDecimal invoice_amount;

    /**
     * 付款金额。用户在咨询中支付的金额。支持小数点后两位。
     */
    private BigDecimal buyer_pay_amount;

    /**
     * 集分宝金额。使用集分宝支付的金额。支持小数点后两位。
     */
    private BigDecimal point_amount;

    /**
     * 总退款金额。退款通知中，返回总退款金额，单位为元，支持小数点后两位。
     */
    private BigDecimal refund_fee;

    /**
     * 实际退款金额。商家实际退款给用户的金额，单位为元，支持小数点后两位。
     */
    private BigDecimal send_back_fee;

    /**
     * 订单标题。商品的标题/交易标题/订单标题/订单关键字等，是请求时对应的参数，原样通知回来。
     */
    private String subject;

    /**
     * 商品描述。该订单的备注、描述、明细等。对应请求时的 body 参数，原样通知回来。
     */
    private String body;

    /**
     * 公共回传参数，如果请求时传递了该参数，则返回给商家时会在异步通知时将该参数原样返回。本参数必须进行 UrlEncode 之后才可以发送给支付宝
     */
    private String passback_params;

    /**
     * 交易创建时间。该笔交易创建的时间。格式 为 yyyy-MM-dd HH:mm:ss。
     */
    private Date gmt_create;

    /**
     * 交易 付款时间。该笔交易的买家付款时间。格式为 yyyy-MM-dd HH:mm:ss。
     */
    private Date gmt_payment;

    /**
     * 交易退款时间。该笔交易的退款时间。格式 为 yyyy-MM-dd HH:mm:ss.SS。
     */
    private Date gmt_refund;

    /**
     * 交易结束时间。该笔交易结束时间。格式为 yyyy-MM-dd HH:mm:ss。
     */
    private Date gmt_close;

    /**
     * 支付金额信息。支付成功的各个渠道金额信息
     */
    private String fund_bill_list;

    /**
     * 优惠券信息。本交易支付时所使用的所有优惠券信息
     */
    private String voucher_detail_list;
}
