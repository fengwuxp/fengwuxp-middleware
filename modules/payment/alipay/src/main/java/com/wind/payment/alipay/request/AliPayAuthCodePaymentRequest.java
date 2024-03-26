package com.wind.payment.alipay.request;

import com.wind.payment.core.request.PrePaymentOrderRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;

/**
 * @author wuxp
 * @date 2023-10-01 17:41
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AliPayAuthCodePaymentRequest extends PrePaymentOrderRequest {

    private static final long serialVersionUID = 6928416287624167924L;

    /**
     * 付款码支付
     */
    @NotBlank
    private String authCode;

    /**
     * 支付场景（条码支付取值：bar_code ,声波支付取值：wave_code）
     */
    private String scene = "bar_code";
}
