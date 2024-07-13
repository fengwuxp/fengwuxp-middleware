package com.wind.server.web.restful;

import com.wind.common.annotations.I18n;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import com.wind.common.i18n.SpringI18nMessageUtils;
import com.wind.common.query.supports.Pagination;
import com.wind.script.spring.SpringExpressionEvaluator;
import com.wind.server.web.supports.ApiResp;
import org.springframework.core.MethodParameter;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 响应之前尝试处理结果对象中被 {@link I18n}注解标记的字段
 * <p>
 * TODO 嵌套对象支持
 *
 * @author wuxp
 * @date 2024-07-11 15:43
 **/
public abstract class AbstractI18nResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Field[] EMPTY = new Field[0];

    private final Map<Class<?>, Field[]> i18nFields = new ConcurrentReferenceHashMap<>();

    private final Locale defaultLocal;

    public AbstractI18nResponseBodyAdvice(Locale defaultLocal) {
        this.defaultLocal = defaultLocal;
    }

    public AbstractI18nResponseBodyAdvice() {
        this(Locale.CHINA);
    }

    @Override
    public boolean supports(MethodParameter returnType, @Nonnull Class converterType) {
        if (Objects.equals(defaultLocal.getLanguage(), SpringI18nMessageUtils.requireLocale().getLanguage())) {
            return false;
        }
        return Objects.requireNonNull(returnType.getMethod()).getReturnType().isAssignableFrom(ApiResp.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType,
                                  @Nonnull Class selectedConverterType, @Nonnull ServerHttpRequest request,
                                  @Nonnull ServerHttpResponse response) {
        if (body instanceof ApiResp) {
            handleReturnValueI18n(((ApiResp<?>) body).getData());
        }
        return body;
    }

    @NotNull
    protected abstract String getI18nMessage(String key);

    private void handleReturnValueI18n(Object result) {
        if (result instanceof Pagination) {
            // 分页对象
            ((Pagination<?>) result).getRecords().forEach(this::fillI18nMessages);
        } else if (result instanceof Collection) {
            // 集合对象
            ((Collection<?>) result).forEach(this::fillI18nMessages);
        } else {
            fillI18nMessages(result);
        }
    }

    private void fillI18nMessages(Object val) {
        if (val == null) {
            return;
        }
        Field[] fields = i18nFields.computeIfAbsent(val.getClass(), this::parseI18nFields);
        for (Field field : fields) {
            try {
                fillI18nMessage(val, field);
            } catch (IllegalAccessException exception) {
                throw new BaseException(DefaultExceptionCode.COMMON_ERROR, "i18n message fill error", exception);
            }
        }
    }

    private void fillI18nMessage(Object val, Field field) throws IllegalAccessException {
        I18n annotation = field.getAnnotation(I18n.class);
        String name = annotation.name();
        String messageKey;
        if (StringUtils.hasText(name)) {
            // 通过 spring expression 解析
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable(I18n.OBJECT_VARIABLE_NAME, val);
            messageKey = SpringExpressionEvaluator.TEMPLATE.eval(name, context);
        } else {
            messageKey = (String) field.get(val);
        }
        String i18nMessage = getI18nMessage(messageKey);
        if (StringUtils.hasText(i18nMessage)) {
            field.set(val, i18nMessage);
        }
    }

    private Field[] parseI18nFields(Class<?> clazz) {
        if (!(clazz.isAnnotationPresent(I18n.class) || clazz.getSuperclass().isAnnotationPresent(I18n.class))) {
            return EMPTY;
        }

        List<Field> clazzFields = getClazzFields(clazz);
        List<Field> result = clazzFields
                .stream()
                .filter(field -> field.isAnnotationPresent(I18n.class))
                .collect(Collectors.toList());
        result.forEach(field -> {
            AssertUtils.isTrue(field.getType() == String.class, "I18n Annotation unsupported String Field");
            field.setAccessible(true);
        });
        return result.toArray(new Field[0]);
    }

    private List<Field> getClazzFields(Class<?> clazz) {
        if (clazz == Object.class) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        result.addAll(getClazzFields(clazz.getSuperclass()));
        return result;
    }
}
