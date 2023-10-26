package com.wind.security.authentication.jwt;

import com.alibaba.fastjson2.JSON;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.wind.common.exception.AssertUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * jwt token 编码解码
 *
 * @author wuxp
 * @date 2023-09-24 16:59
 **/
public final class JwtTokenCodec {

    private static final String AUTHENTICATION_VARIABLE_NAME = "authentication";

    private static final String JWT_AUTH_KEY_ID = "jwt-auth-codec-kind";

    private final JwtProperties properties;

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();

    public JwtTokenCodec(JwtProperties properties) {
        this.properties = properties;
        RSAKey rsaKey = generateRsaKey(properties.getKeyPair());
        this.jwtEncoder = buildJwtEncoder(rsaKey);
        this.jwtDecoder = buildJwtDecoder(rsaKey);
    }

    /**
     * jwt parse token
     *
     * @param jwtToken jwt token
     * @return jwt token payload
     */
    @Nullable
    public JwtTokenPayload parse(String jwtToken) {
        if (StringUtils.hasLength(jwtToken)) {
            Jwt jwt = jwtDecoder.decode(jwtToken);
            Map<String, Object> claims = jwt.getClaims();
            JwtUser user = JSON.to(properties.getUserType(), claims.get(AUTHENTICATION_VARIABLE_NAME));
            Instant expiresAt = jwt.getExpiresAt();
            AssertUtils.notNull(expiresAt, "jwt token expire must not null");
            return new JwtTokenPayload(jwt.getSubject(), user, expiresAt.toEpochMilli());
        }
        return null;
    }

    /**
     * 生成用户 token
     *
     * @param user 用户信息
     * @return 用户 token
     */
    public String encoding(JwtUser user) {
        Jwt jwt = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        jwsHeader,
                        newJwtBuilder(String.valueOf(user.getId()))
                                .claim(AUTHENTICATION_VARIABLE_NAME, user)
                                .build()
                )
        );
        return jwt.getTokenValue();
    }

    /**
     * 生成 refresh token
     *
     * @param userId 用户 id
     * @return refresh token
     */
    public String encodingRefreshToken(Long userId) {
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, newJwtBuilder(String.valueOf(userId)).build()));
        return jwt.getTokenValue();
    }

    /**
     * 解析 验证 refresh token
     *
     * @param refreshToken refresh token
     * @return 用户 id
     */
    @Nullable
    public JwtTokenPayload parseRefreshToken(String refreshToken) {
        if (StringUtils.hasLength(refreshToken)) {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            Instant expiresAt = jwt.getExpiresAt();
            AssertUtils.notNull(expiresAt, "jwt token expire must not null");
            return new JwtTokenPayload(jwt.getSubject(), null, expiresAt.toEpochMilli());
        }
        return null;
    }

    private JwtClaimsSet.Builder newJwtBuilder(String userId) {
        return JwtClaimsSet.builder()
                .expiresAt(Instant.now().plusSeconds(properties.getEffectiveTime().getSeconds()))
                .audience(Collections.singletonList(properties.getAudience()))
                .issuer(properties.getIssuer())
                .subject(userId);
    }

    private JwtDecoder buildJwtDecoder(RSAKey rsaKey) {
        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, new ImmutableJWKSet<>(new JWKSet(rsaKey))));
        return new NimbusJwtDecoder(processor);
    }

    private JwtEncoder buildJwtEncoder(RSAKey rsaKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    private RSAKey generateRsaKey(KeyPair keyPair) {
        // https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-jose/src/test/java/org/springframework/security/oauth2/jose/TestKeys.java
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(JWT_AUTH_KEY_ID)
                .build();
    }

}
