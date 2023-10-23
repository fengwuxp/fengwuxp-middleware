package com.wind.security.core.rbac;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;

/**
 * @author wuxp
 * @date 2023-10-23 09:47
 **/
public interface RbacResourceCacheSupplier extends BiFunction<String, Executor, RbacResourceCache<String, Object>> {
}
