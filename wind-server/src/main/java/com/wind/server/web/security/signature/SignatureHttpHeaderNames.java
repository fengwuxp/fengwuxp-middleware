package com.wind.server.web.security.signature;

import com.wind.common.WindConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import static com.wind.server.web.security.signature.SignatureConstants.ACCESS_KEY_HEADER_NAME;
import static com.wind.server.web.security.signature.SignatureConstants.DEBUG_SIGN_CONTENT_HEADER_NAME;
import static com.wind.server.web.security.signature.SignatureConstants.DEBUG_SIGN_HEADER_NAME;
import static com.wind.server.web.security.signature.SignatureConstants.NONCE_HEADER_NAME;
import static com.wind.server.web.security.signature.SignatureConstants.SIGN_HEADER_NAME;
import static com.wind.server.web.security.signature.SignatureConstants.TIMESTAMP_HEADER_NAME;

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

    final String accessKey;

    final String sign;

    final String debugSign;

    final String debugSignContent;

    public SignatureHttpHeaderNames() {
        this(WindConstants.WIND);
    }

    public SignatureHttpHeaderNames(String headerPrefix) {
        this(
                getHeaderName(headerPrefix, NONCE_HEADER_NAME),
                getHeaderName(headerPrefix, TIMESTAMP_HEADER_NAME),
                getHeaderName(headerPrefix, ACCESS_KEY_HEADER_NAME),
                getHeaderName(headerPrefix, SIGN_HEADER_NAME),
                getHeaderName(headerPrefix, DEBUG_SIGN_HEADER_NAME),
                getHeaderName(headerPrefix, DEBUG_SIGN_CONTENT_HEADER_NAME)
        );
    }

    private static String getHeaderName(String headerPrefix, String headerName) {
        if (StringUtils.hasLength(headerName)) {
            return headerPrefix + WindConstants.DASHED + headerName;
        }
        return headerName;
    }
}
