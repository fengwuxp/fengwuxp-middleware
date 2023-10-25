package com.wind.context.injection;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 上下文变量提供者
 *
 * @author wuxp
 * @date 2023-10-24 19:00
 **/
public interface ContextVariableProvider extends Supplier<Map<String, Object>> {


}
