package com.wind.payment.core;

import java.util.function.Function;

/**
 * 收款商户配置提供者
 *
 * @author wuxp
 * @date 2023-09-30 19:08
 **/
public interface PartnerConfigProvider extends Function<String, String> {

}
