package com.wind.logging.logback.mask;

import com.wind.mask.MaskRule;
import com.wind.mask.ObjectMasker;
import com.wind.mask.masker.TextRangMasker;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wind.logging.logback.mask.MaskingMessageConverter.LOG_MASKER;

/**
 * @author wuxp
 * @date 2024-08-07 15:51
 **/
@Slf4j
class MaskingMessageConverterTests {

    private static final Logger LOG = LoggerFactory.getLogger(MaskingMessageConverterTests.class);

    @Test
    void testLogin() {
        LOG_MASKER.registerRule(LogbackMaskUser.class, MaskRule.mark(LogbackMaskUser.Fields.mobilePhone, TextRangMasker.phone()));
        LOG_MASKER.registerRule(LogbackMaskUser.class, MaskRule.mark(LogbackMaskUser.Fields.password, TextRangMasker.secret()));
        LogbackMaskUser user = new LogbackMaskUser();
        String mobilePhone = "18900234567";
        user.setMobilePhone(mobilePhone);
        user.setPassword("123213");
        LOG.info("test = {}", user);
        Assertions.assertEquals(mobilePhone, user.getMobilePhone());
    }

    @Data
    @FieldNameConstants
    static class LogbackMaskUser {

        private String mobilePhone;

        private String password;
    }
}
