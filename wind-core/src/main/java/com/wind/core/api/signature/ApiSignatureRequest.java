package com.wind.core.api.signature;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import com.wind.common.exception.BaseException;
import com.wind.common.exception.DefaultExceptionCode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * API 签名加签对象
 *
 * @author wuxp
 * @date 2023-10-18 22:08
 */
@Data
@Getter
@Builder
@FieldNameConstants
public class ApiSignatureRequest {

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
    private final String canonicalizedQueryString;

    /**
     * 请求体
     */
    private final String requestBody;

    /**
     * 签名版本 v2版本查询字符需要排序
     * {@link #buildCanonicalizedQueryString}
     */
    @Deprecated
    private final String version;

    private ApiSignatureRequest(String method, String requestPath, String nonce, String timestamp, String decodeQueryString, String requestBody, String version) {
        AssertUtils.hasText(method, "method must not empty");
        AssertUtils.hasText(requestPath, "requestPath must not empty");
        AssertUtils.hasText(nonce, "nonce must not empty");
        AssertUtils.hasText(timestamp, "timestamp must not empty");
        this.method = method.toUpperCase();
        this.requestPath = requestPath;
        this.nonce = nonce;
        this.timestamp = timestamp;
        // 如果是 v2 版本，则将查询字符串 key 按照字典序排序
        this.canonicalizedQueryString = Objects.equals(version, "v2") ? buildCanonicalizedQueryString(parseQueryParamsAsMap(decodeQueryString)) : decodeQueryString;
        this.requestBody = requestBody;
        this.version = version;
    }

    /**
     * @return 获取摘要签名字符串
     */
    public String getSignTextForDigest() {
        StringBuilder result = new StringBuilder()
                .append(Fields.method).append(WindConstants.EQ).append(method).append(WindConstants.AND)
                .append(Fields.requestPath).append(WindConstants.EQ).append(requestPath).append(WindConstants.AND)
                .append(Fields.nonce).append(WindConstants.EQ).append(nonce).append(WindConstants.AND)
                .append(Fields.timestamp).append(WindConstants.EQ).append(timestamp);
        if (StringUtils.hasLength(canonicalizedQueryString)) {
            result.append(WindConstants.AND)
                    .append(String.format("%s%s", Fields.canonicalizedQueryString, MD5_TAG))
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(canonicalizedQueryString.getBytes(StandardCharsets.UTF_8)));
        }
        if (StringUtils.hasLength(requestBody)) {
            result.append(WindConstants.AND)
                    .append(String.format("%s%s", Fields.requestBody, MD5_TAG))
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(requestBody.getBytes(StandardCharsets.UTF_8)));
        }
        return result.toString();
    }

    /**
     * @return 获取 Sha256WithRsa 签名字符串
     */
    public String getSignTextForSha256WithRsa() {
        return method + WindConstants.SPACE + requestPath + WindConstants.LF +
                timestamp + WindConstants.LF +
                nonce + WindConstants.LF +
                (StringUtils.hasText(canonicalizedQueryString) ? canonicalizedQueryString : WindConstants.EMPTY) + WindConstants.LF +
                (StringUtils.hasText(requestBody) ? requestBody : WindConstants.EMPTY) + WindConstants.LF;
    }

    /**
     * @return 获取按照字典序的查询字符串
     */
    @Nullable
    private String buildCanonicalizedQueryString(Map<String, String[]> queryParams) {
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

    private Map<String, String[]> parseQueryParamsAsMap(String queryString) {
        Map<String, String[]> result = new HashMap<>();
        if (StringUtils.hasText(queryString)) {
            String[] parts = decodeQueryString(queryString).split(WindConstants.AND);
            for (String part : parts) {
                String[] keyValue = part.split(WindConstants.EQ);
                if (keyValue.length == 2) {
                    result.put(keyValue[0], new String[]{keyValue[1]});
                } else {
                    result.put(keyValue[0], new String[0]);
                }
            }
        }
        return result;
    }

    @NotNull
    private String decodeQueryString(String queryString) {
        try {
            // @see https://juejin.cn/post/6844904034453864462#heading-2
            return queryString == null ? null : URLDecoder.decode(queryString.replace("+", "%20"), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException exception) {
            throw new BaseException(DefaultExceptionCode.BAD_REQUEST, "decode url error", exception);
        }
    }
}
