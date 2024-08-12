package com.wind.logging.logback.mask;

import com.wind.common.exception.BaseException;
import com.wind.mask.MaskRuleGroup;
import com.wind.mask.masker.StringRangMasker;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author wuxp
 * @date 2024-08-07 15:51
 **/
@Slf4j
class MaskingMessageConverterTests {

    private static final Logger LOG = LoggerFactory.getLogger(MaskingMessageConverterTests.class);

    @Test
    void testLogin() {
        List<MaskRuleGroup> groups = MaskRuleGroup.builder().form(LogbackMaskUser.class)
                .of(LogbackMaskUser.Fields.mobilePhone, StringRangMasker.phone())
                .of(LogbackMaskUser.Fields.password, StringRangMasker.secret())
                .build();
        LogbackMaskUser user = new LogbackMaskUser();
        String mobilePhone = "18900234567";
        user.setMobilePhone(mobilePhone);
        user.setPassword("123213");
        LOG.info("test = {}", user);
        LOG.error("test = {}", user, BaseException.badRequest("Bad Request"));
        Assertions.assertEquals(mobilePhone, user.getMobilePhone());
    }

    @Data
    @FieldNameConstants
    static class LogbackMaskUser {

        private String mobilePhone;

        private String password;
    }
}
