package com.wind.payment.core.response;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wuxp
 * @date 2023-09-30 19:34
 **/
@Data
@Accessors(chain = true)
public class PrePaymentOrderResponse implements Serializable {

    private static final long serialVersionUID = -6545970091470284620L;

    /**
     * 支付交易流水号(系统内的)
     */
    @NotNull
    private String transactionNo;

    /**
     * 第三方交易流水号
     * <p>
     * 1：由于支付宝App支付并没有发起真正预下单，该字段要从回掉中获取
     * </p>
     */
    private String outTransactionNo;


    /**
     * 订单金额
     * 单位分
     */
    private Integer orderAmount;

    /**
     * 是否沙箱环境
     */
    private Boolean useSandboxEnv = false;

    /**
     * 预下单结果内容
     */
    private Object result;

    /**
     * 原始响应
     */
    private Object rawResponse;

    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public <T> T getRawResponse() {
        return (T) rawResponse;
    }
}
