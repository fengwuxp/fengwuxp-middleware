package com.wind.api.core.signature;

import com.wind.common.WindConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 用于自定义请求头名称
 *
 * @author wuxp
 * @date 2023-10-22 08:24
 **/
@Getter
@AllArgsConstructor
public final class SignatureHttpHeaderNames {

    final String nonce;

    final String timestamp;

    final String accessId;

    final String secretVersion;

    final String sign;

    final String debugSignContent;

    final String debugSignQuery;

    public SignatureHttpHeaderNames() {
        this(WindConstants.WIND);
    }

    public SignatureHttpHeaderNames(String headerPrefix) {
        this(
                getHeaderName(headerPrefix, SignatureConstants.NONCE_HEADER_NAME),
                getHeaderName(headerPrefix, SignatureConstants.TIMESTAMP_HEADER_NAME),
                getHeaderName(headerPrefix, SignatureConstants.ACCESS_ID_HEADER_NAME),
                getHeaderName(headerPrefix, SignatureConstants.SECRET_VERSION_HEADER_NAME),
                getHeaderName(headerPrefix, SignatureConstants.SIGN_HEADER_NAME),
                SignatureConstants.DEBUG_SIGN_CONTENT_HEADER_NAME,
                SignatureConstants.DEBUG_SIGN_QUERY_HEADER_NAME
        );
    }

    private static String getHeaderName(String headerPrefix, String headerName) {
        if (!StringUtils.hasText(headerPrefix)) {
            headerPrefix = WindConstants.WIND;
        }
        return headerPrefix + WindConstants.DASHED + headerName;
    }
}
