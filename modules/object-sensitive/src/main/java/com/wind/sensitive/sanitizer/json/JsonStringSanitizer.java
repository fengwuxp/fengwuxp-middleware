package com.wind.sensitive.sanitizer.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONPath;
import com.wind.sensitive.ObjectSanitizePrinter;
import com.wind.sensitive.ObjectSanitizer;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * json text 脱敏
 *
 * @author wuxp
 * @date 2024-08-02 15:04
 **/
public final class JsonStringSanitizer implements ObjectSanitizer<String, String> {

    @Override
    public String sanitize(String json, Collection<String> keys) {
        if (StringUtils.hasText(json)) {
            Object val = JSON.parse(json);
            keys.forEach(key -> {
                try {
                    Object eval = JSONPath.eval(val, key);
                    if (eval != null) {
                        JSONPath.set(val, key, ObjectSanitizePrinter.ASTERISK.sanitize(eval));
                    }
                } catch (Exception exception) {
                    // ignore
                }
            });
            return JSON.toJSONString(val);
        }
        return json;
    }
}