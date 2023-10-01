package com.wind.payment.core;

import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import lombok.AllArgsConstructor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuxp
 * @date 2023-09-30 20:17
 **/
@AllArgsConstructor
public class DefaultPaymentTransactionPluginFactory implements PaymentTransactionPluginFactory {

    private static final Map<String, Class<? extends PaymentTransactionPlugin>> PLUGINS = new ConcurrentHashMap<>();

    private final PartnerConfigProvider partnerConfigProvider;

    @Override
    public PaymentTransactionPlugin factory(String partnerId, PaymentTransactionPlatform platform, PaymentTransactionScene scene) {
        Class<? extends PaymentTransactionPlugin> classType = PLUGINS.get(getPluginCacheKey(platform, scene));
        AssertUtils.notNull(classType, String.format("not found platform = %s scene = %s payment transaction plugin", platform.getDesc(), scene.getDesc()));
        String config = partnerConfigProvider.apply(partnerId);
        return buildPaymentTransactionPlugin(classType, config);
    }

    private PaymentTransactionPlugin buildPaymentTransactionPlugin(Class<? extends PaymentTransactionPlugin> classType, String config) {
        try {
            Constructor<? extends PaymentTransactionPlugin> constructor = ReflectionUtils.accessibleConstructor(classType, String.class);
            return constructor.newInstance(config);
        } catch (Exception exception) {
            throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "build PaymentTransactionPlugin error", exception);
        }
    }

    /**
     * 注册支付插件实现
     *
     * @param platform        支付平台
     * @param scene           支付场景
     * @param pluginClassType 支付插件实现类
     */
    public static void register(PaymentTransactionPlatform platform, PaymentTransactionScene scene, Class<? extends PaymentTransactionPlugin> pluginClassType) {
        PLUGINS.put(getPluginCacheKey(platform, scene), pluginClassType);
    }

    private static String getPluginCacheKey(PaymentTransactionPlatform platform, PaymentTransactionScene scene) {
        return String.format("%s_%s", platform.name(), scene.name());
    }
}
