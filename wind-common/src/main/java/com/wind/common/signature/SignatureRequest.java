package com.wind.common.signature;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 加签请求
 *
 * @author wuxp
 * @date 2023-10-18 22:08
 */
@Data
@Getter
@Builder
@FieldNameConstants
public class SignatureRequest {

    private static final String MD5_TAG = "Md5";

    /**
     * http 请求方法
     */
    private final String method;

    /**
     * http 请求 path，不包含查询参数和域名
     */
    private final String requestPath;

    /**
     * 32 位字符串
     */
    private final String nonce;

    /**
     * 时间戳
     */
    private final String timestamp;

    /**
     * 请求查询字符串
     */
    @Deprecated
    private final String queryString;

    /**
     * 查询参数
     */
    private final Map<String, String[]> queryParams;

    /**
     * 请求体
     */
    private final String requestBody;

    /**
     * sk
     */
    private final String secretKey;

    private SignatureRequest(String method, String requestPath, String nonce, String timestamp, String queryString, Map<String, String[]> queryParams, String requestBody, String secretKey) {
        AssertUtils.notNull(method, "method must not empty");
        AssertUtils.notNull(requestPath, "requestPath must not empty");
        AssertUtils.notNull(nonce, "nonce must not empty");
        AssertUtils.notNull(timestamp, "timestamp must not empty");
        AssertUtils.notNull(secretKey, "secretKey must not empty");
        this.method = method.toUpperCase();
        this.requestPath = requestPath;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.queryString = queryString;
        this.queryParams = queryParams == null ? null : new TreeMap<>(queryParams);
        this.requestBody = requestBody;
        this.secretKey = secretKey;
    }

    /**
     * @return 获取签名字符串
     */
    public String getSignText() {
        StringBuilder builder = new StringBuilder()
                .append(Fields.method).append(WindConstants.EQ).append(method).append(WindConstants.AND)
                .append(Fields.requestPath).append(WindConstants.EQ).append(requestPath).append(WindConstants.AND)
                .append(Fields.nonce).append(WindConstants.EQ).append(nonce).append(WindConstants.AND)
                .append(Fields.timestamp).append(WindConstants.EQ).append(timestamp);
        String canonicalizedQueryString = getCanonicalizedQueryString();
        if (StringUtils.hasLength(canonicalizedQueryString)) {
            builder.append(WindConstants.AND)
                    .append(String.format("%s%s", Fields.queryString, MD5_TAG))
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(canonicalizedQueryString.getBytes(StandardCharsets.UTF_8)));
        }
        if (StringUtils.hasLength(requestBody)) {
            builder.append(WindConstants.AND)
                    .append(String.format("%s%s", Fields.requestBody, MD5_TAG))
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(requestBody.getBytes(StandardCharsets.UTF_8)));
        }
        return builder.toString();
    }

    /**
     * @return 获取按照字典序的查询字符串
     */
    @Nullable
    public String getCanonicalizedQueryString() {
        if (StringUtils.hasLength(queryString)) {
            // 兼容模式  TODO 待删除
            return queryString;
        }
        if (ObjectUtils.isEmpty(queryParams)) {
            return null;
        }
        return queryParams.entrySet()
                .stream()
                .map(entry -> {
                    if (ObjectUtils.isEmpty(entry.getValue())) {
                        return null;
                    }
                    return Arrays.stream(entry.getValue())
                            .map(val -> String.format("%s=%s", entry.getKey(), val))
                            .collect(Collectors.joining(WindConstants.AND));
                })
                .collect(Collectors.joining(WindConstants.AND));
    }
}
