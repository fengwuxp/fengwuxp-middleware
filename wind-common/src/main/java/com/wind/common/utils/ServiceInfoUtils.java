package com.wind.common.utils;

import com.google.common.collect.ImmutableSet;
import com.wind.common.WindConstants;
import com.wind.common.spring.ApplicationContextUtils;

import java.util.Set;

/**
 * 服务信息相关工具
 *
 * @author wuxp
 * @date 2023-10-02 09:17
 **/
public final class ServiceInfoUtils {

    private static final Set<String> ONLINE_ENVS = ImmutableSet.of(WindConstants.PRE, WindConstants.PROD);

    private ServiceInfoUtils() {
        throw new AssertionError();
    }

    /**
     * @return 是否线上环境
     */
    public static boolean isOnline() {
        return ONLINE_ENVS.contains(getSpringProfilesActive());
    }

    private static String getSpringProfilesActive() {
        return ApplicationContextUtils.getProperty(WindConstants.SPRING_PROFILES_ACTIVE);
    }
}
