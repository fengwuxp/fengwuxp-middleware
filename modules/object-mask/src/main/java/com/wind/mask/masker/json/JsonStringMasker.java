package com.wind.mask.masker.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONPath;
import com.wind.mask.ObjectMaskPrinter;
import com.wind.mask.ObjectMasker;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * json text 脱敏
 *
 * @author wuxp
 * @date 2024-08-02 15:04
 **/
public final class JsonStringMasker implements ObjectMasker<String, String> {

    @Override
    public String mask(String json, Collection<String> keys) {
        if (StringUtils.hasText(json)) {
            Object val = JSON.parse(json);
            keys.forEach(key -> {
                try {
                    Object eval = JSONPath.eval(val, key);
                    if (eval != null) {
                        JSONPath.set(val, key, ObjectMaskPrinter.ASTERISK.mask(eval));
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