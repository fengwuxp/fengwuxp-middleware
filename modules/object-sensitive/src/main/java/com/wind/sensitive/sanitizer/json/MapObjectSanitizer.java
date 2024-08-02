package com.wind.sensitive.sanitizer.json;


import com.alibaba.fastjson2.JSONPath;
import com.wind.sensitive.ObjectSanitizePrinter;
import com.wind.sensitive.ObjectSanitizer;

import java.util.Collection;
import java.util.Map;

/**
 * Map 对象脱敏
 *
 * @author wuxp
 * @date 2024-08-02 15:03
 **/
public final class MapObjectSanitizer implements ObjectSanitizer<Map<String, Object>, Object> {

    @Override
    public Object sanitize(Map<String, Object> obj, Collection<String> keys) {
        if (obj == null || obj.isEmpty()) {
            return obj;
        }
        for (String key : keys) {
            try {
                Object eval = JSONPath.eval(obj, key);
                if (eval != null) {
                    JSONPath.set(obj, key, ObjectSanitizePrinter.ASTERISK.sanitize(eval));
                }
            } catch (Exception exception) {
                // ignore
            }
        }
        return obj;
    }
}
