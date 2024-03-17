package com.wind.security.mfa;

import com.warrenstrange.googleauth.ICredentialRepository;
import com.wind.common.WindConstants;
import com.wind.common.exception.AssertUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuxp
 * @date 2024-03-05 15:39
 **/
class GoogleTotptAuthenticatorTests {

    private final Map<String, String> secrets = new HashMap<>();

    private final TotpAuthenticator authenticator = new GoogleTotpAuthenticator(new ICredentialRepository() {
        @Override
        public String getSecretKey(String userName) {
            String result = secrets.get(userName);
            AssertUtils.hasText(result, String.format("get username =%s SecretKey error", userName));
            return result;
        }

        @Override
        public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
            secrets.put(userName, secretKey);
        }
    });

    @Test
    void testGenerateBindingQrCode() {
        String result = authenticator.generateBindingQrCode("1", "zhans", WindConstants.WIND);
        Assertions.assertNotNull(result);
    }
}
