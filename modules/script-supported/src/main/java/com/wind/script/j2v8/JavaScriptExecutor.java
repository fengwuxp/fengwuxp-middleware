package com.wind.script.j2v8;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.util.TypeUtils;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8ScriptException;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import com.wind.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 使用 V8 引擎执行 JavaScript 代码
 * <a href="https://github.com/eclipsesource/J2V8">J2V8 GitHub</a>
 *
 * @author wuxp
 * @version JavaScriptExecutor.java, v 0.1 2022年09月29日 11:06 wuxp
 * @doc <a href="https://eclipsesource.com/blogs/tutorials/getting-started-with-j2v8/">getting-started-with-j2v8</a>
 */
@Slf4j
public final class JavaScriptExecutor {

    /**
     * js 异常中的错误前缀
     */
    private static final String JS_ERROR_PREFIX = "Error: ";

    /**
     * 需要执行的 js lib 代码
     */
    private static final List<String> JS_LIB_SCRIPTS = loadJsLibScripts();

    /**
     * 保存 esnext 语法特性的 polyfill js 代码
     */
    @VisibleForTesting
    static final AtomicReference<String> POLYFILL_JS = new AtomicReference<>();

    private JavaScriptExecutor() {
        throw new AssertionError();
    }

    /**
     * 使用闭包的方式执行 js 函数
     * example:
     * <code>
     * ((...args)=>{
     * // do something
     * })(...args)
     * </code>
     * 如果需要将 js 返回值转换为具体对象，请使用
     * {@link #executeFunctionUseClosure(String, ParameterizedTypeReference, Object...)}
     * {@link #executeFunctionUseClosure(String, Class, Object...)}
     *
     * @param functionCode js 函数代码
     * @param invoker      JsExecJavaInvoker 对象
     * @param args         js 函数参数
     * @return js 方法执行结果 Map、List 、字符串、数字、布尔值
     */
    @SuppressWarnings("unchecked")
    public static <T> T executeFunctionSupportJavaUseClosure(String functionCode, JsExecJavaMethodInvoker invoker, Object... args) {
        StringBuilder code = new StringBuilder();
        code.append('(').append(functionCode).append(')').append('(');
        for (Object o : args) {
            if (ClassUtils.isPrimitiveOrWrapper(o.getClass())) {
                code.append(o);
            } else {
                code.append(JSON.toJSONString(o));
            }
            code.append(",");
        }
        if (args.length > 0) {
            code.deleteCharAt(code.length() - 1);
        }
        code.append(')');
        // 避免多线程执行出错，@see ：https://github.com/eclipsesource/J2V8/issues/330
        V8 v8 = V8.createV8Runtime();
        List<Object> releaseObjects = new ArrayList<>();
        try {
            releaseObjects.add(addPolyfillJs(v8));
            // 加载 js lib
            releaseObjects.addAll(JS_LIB_SCRIPTS.stream().map(v8::executeScript).collect(Collectors.toList()));
            if (invoker != null) {
                releaseObjects.addAll(registerJsExecJavaInvoker(v8, invoker));
            }
            Object result = v8.executeScript(code.toString());
            releaseObjects.add(result);
            return (T) fixUndefinedToNull(convertV8Object(result));
        } catch (Exception e) {
            if (e instanceof V8ScriptException) {
                // 转换 js 脚本执行异常的 message
                String message = ((V8ScriptException) e).getJSMessage();
                // 剪切掉错误消息的前缀
                message = message != null && message.startsWith(JS_ERROR_PREFIX) ? message.substring(JS_ERROR_PREFIX.length()) : message;
                throw new RuntimeException(message, e);
            }
            throw e;
        } finally {
            // v8 对象最后释放
            releaseObjects.add(v8);
            releaseV8Object(releaseObjects.toArray(new Object[0]));
        }
    }

    /**
     * 使用闭包的方式执行 js 函数
     *
     * @param functionCode js 函数代码
     * @param args         js 函数参数
     * @return js 方法执行结果 Map、List 、字符串、数字、布尔值
     */
    public static <T> T executeFunctionUseClosure(String functionCode, Object... args) {
        return executeFunctionSupportJavaUseClosure(functionCode, null, args);
    }

    private static Object addPolyfillJs(V8 v8) {
        String polyFillJs = POLYFILL_JS.get();
        if (StringUtils.hasText(polyFillJs)) {
            return v8.executeScript(polyFillJs);
        }
        return null;
    }

    private static Object convertV8Object(Object value) {
        if (isUndefined(value)) {
            return null;
        }
        if (value instanceof V8Array) {
            return V8ObjectUtils.toList((V8Array) value);
        }
        if (value instanceof V8Object) {
            return V8ObjectUtils.toMap((V8Object) value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static Object fixUndefinedToNull(Object value) {
        if (value instanceof Map) {
            Map<String, ?> maps = (Map<String, ?>) value;
            Map<String, Object> newValues = new HashMap<>(maps.size());
            maps.forEach((key, val) -> newValues.put(key, fixUndefinedToNull(val)));
            return newValues;
        }
        if (value instanceof List) {
            return ((List<?>) value).stream()
                    .map(JavaScriptExecutor::fixUndefinedToNull)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return isUndefined(value) ? null : value;
    }

    private static void releaseV8Object(Object... objects) {
        for (Object o : objects) {
            if (o instanceof V8Object) {
                ((V8Object) o).release();
            }
        }
    }

    private static boolean isUndefined(Object result) {
        return result instanceof V8Object && ((V8Object) result).isUndefined();
    }

    @Nullable
    public static <T> T executeFunctionUseClosure(String functionCode, ParameterizedTypeReference<T> type, Object... args) {
        return executeFunctionAndParseResult(functionCode, type.getType(), args);
    }

    @Nullable
    public static <T> T executeFunctionUseClosure(String functionCode, Class<T> clazz, Object... args) {
        return executeFunctionAndParseResult(functionCode, clazz, args);
    }

    @Nullable
    private static <T> T executeFunctionAndParseResult(String functionCode, Type type, Object... args) {
        Object result = executeFunctionUseClosure(functionCode, args);
        if (result == null) {
            // TypeUtils#cast 对于基本数据类型在 result 为 null 时会返回默认值，所以这里提前返回
            return null;
        }
        return TypeUtils.cast(result, type);
    }

    private static List<String> loadJsLibScripts() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:jslib/*.min.js");
            List<String> result = new ArrayList<>();
            for (Resource resource : resources) {
                // 由于文件打包后在 jar 中，使用流的方式读取
                try (InputStream inputStream = resource.getInputStream()) {
                    result.add(StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8));
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("load js lib failure", e);
        }
    }

    private static List<V8Object> registerJsExecJavaInvoker(V8 v8, JsExecJavaMethodInvoker invoker) {
        List<V8Object> releaseObjects = new ArrayList<>(3);
        V8Object javaInvoker = new V8Object(v8);
        v8.add(invoker.getJsObjectName(), javaInvoker);
        releaseObjects.add(javaInvoker);
        // 注册 Java 方法
        for (JsExecJavaMethodInvoker.JavaMethodDescriptor descriptor : invoker.getJavaMethodDescriptors()) {
            releaseObjects.add(javaInvoker.registerJavaMethod(invoker, descriptor.getJavaMethodName(), descriptor.getJsFunctionName(),
                    descriptor.getParameterTypes()));
        }
        return releaseObjects;
    }

    public static void configure(String polyfillJs) {
        POLYFILL_JS.set(polyfillJs);
    }

}