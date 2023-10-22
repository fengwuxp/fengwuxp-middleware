package com.wind.common.signature;

import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

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
    private final String queryString;

    /**
     * 请求体
     */
    private final String requestBody;

    /**
     * sk
     */
    private final String secretKey;

    private SignatureRequest(String method, String requestPath, String nonce, String timestamp, String queryString, String requestBody, String secretKey) {
        AssertUtils.notNull(method, "method must not empty");
        AssertUtils.notNull(requestPath, "requestPath must not empty");
        AssertUtils.notNull(nonce, "nonce must not empty");
        AssertUtils.notNull(timestamp, "timestamp must not empty");
        AssertUtils.notNull(secretKey, "secretKey must not empty");
        this.method = method;
        this.requestPath = requestPath;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.queryString = queryString;
        this.requestBody = requestBody;
        this.secretKey = secretKey;
    }

    /**
     * @return 获取签名字符串
     */
    public String getSignText() {
        AssertUtils.isTrue(StringUtils.hasLength(nonce) && StringUtils.hasLength(timestamp), "验签请缺少必要参数");
        StringBuilder builder = new StringBuilder()
                .append(Fields.method).append(WindConstants.EQ).append(method).append(WindConstants.AND)
                .append(Fields.requestPath).append(WindConstants.EQ).append(requestPath).append(WindConstants.AND)
                .append(Fields.nonce).append(WindConstants.EQ).append(nonce).append(WindConstants.AND)
                .append(Fields.timestamp).append(WindConstants.EQ).append(timestamp);
        if (StringUtils.hasLength(queryString)) {
            builder.append(WindConstants.AND)
                    .append(Fields.queryString)
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(queryString.getBytes(StandardCharsets.UTF_8)));
        }
        if (StringUtils.hasLength(requestBody)) {
            builder.append(WindConstants.AND)
                    .append(Fields.requestBody)
                    .append(WindConstants.EQ)
                    .append(DigestUtils.md5DigestAsHex(requestBody.getBytes(StandardCharsets.UTF_8)));
        }
        return builder.toString();
    }
}
