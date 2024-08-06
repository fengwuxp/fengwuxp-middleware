package com.wind.mask.masker.json;


import com.alibaba.fastjson2.JSONPath;
import com.wind.mask.ObjectMaskPrinter;
import com.wind.mask.ObjectMasker;

import java.util.Collection;
import java.util.Map;

/**
 * Map 对象脱敏
 *
 * @author wuxp
 * @date 2024-08-02 15:03
 **/
public final class MapObjectMasker implements ObjectMasker<Map<String, Object>, Object> {

    @Override
    public Object mask(Map<String, Object> obj, Collection<String> keys) {
        if (obj == null || obj.isEmpty()) {
            return obj;
        }
        for (String key : keys) {
            try {
                Object eval = JSONPath.eval(obj, key);
                if (eval != null) {
                    JSONPath.set(obj, key, ObjectMaskPrinter.ASTERISK.mask(eval));
                }
            } catch (Exception exception) {
                // ignore
            }
        }
        return obj;
    }
}
